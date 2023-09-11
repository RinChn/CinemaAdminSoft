package sp.back;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.FileDialog;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import org.apache.log4j.Logger;
import org.w3c.dom.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;

import sp.entities.Film;
import sp.entities.Info_Film;
import sp.entities.Session;

/**
 * Запись данных из таблицы в файл.
 * @author Арина Морик 
 */
public class FileRead extends JFrame {
	
	/** Интерфейс для работы с JPA **/
	private static final EntityManagerFactory emf = Singleton.getInstance().getEntityManagerFactory();
	
	/** Номер месяца, сеансы которого записываются в файл **/
	private int monthNumb;
	
	/** Название последнего прочитанного из файла фильма**/ 
	private String lastTitle;
	
	/** Наименование файла, из которого происходит загрузка данных **/
	private String fileNameRead;
	
	/** Количество строк в обновляемой таблице. **/
	private int countRow;
	
	/** Логгер класса **/
	private static final Logger log = Logger.getLogger(FileRead.class.getName());
	
	/**
	 * Конструктор класса.
	 * @param windowName - название окна.
	 * @param data - модель таблицы, в которую происходит загрузка данных.
	 * @param modeAndMonth - массив из номера месяца и режима загрузки: 
	 * 								JOptionPane.NO_OPTION - полное обновление уже существующего списка, 
	 * 								 иначе - добавление новых данных к уже существующему списку. 
	 * @param checkColumn - список столбцов, по которым добавляемые элементы не должны совпадать с уже существующими.
	 * @param columnNames - названия столбцов читаемой таблицы на латинице в .xml файле.
	 * @param nameElem - наименование элемента в таблице, сохраненной в .xml файле.
	 */
	public FileRead(String windowName, DefaultTableModel data, int[] modeAndMonth, int[] checkColumn, String[] columnNames, String nameElem) {
		
		this.monthNumb = modeAndMonth[1];
		
		// Создание окна выбора файла для загрузки
		FileDialog window = new FileDialog(this, windowName, FileDialog.LOAD);
		window.setVisible(true);
		 
		// Определение имени файла, выбранного пользователем.
		fileNameRead = window.getDirectory() + window.getFile();
		if (window.getDirectory() != null) {
			
			 log.info("Загрузка данных из файла " + fileNameRead);
			 
			 try {
				 checkFileName(fileNameRead);
				 
				 // Очищение таблицы, если пользователь выбрал полную замену информации 
				 // из таблицы в приложении на таблицу из файла.
				 countRow = data.getRowCount();
				 if (modeAndMonth[0] == JOptionPane.NO_OPTION && data.getRowCount() != 0) {
					 log.debug("Очищение таблицы в приложении.");
					 
					 // Если пользователь выбрал полное обновление таблицы, то и в случае репертуара, 
					 // и в случае таблицы сеансов все сеансы удаляются потому, 
					 // что если удаляется фильм из репертуара, то удаляются все сеансы с ним в этом месяце.
					 OperationsWithDatabase.deleteAllSessions(monthNumb);
					 
					 for(int i = 0; i < countRow; i++)  
						 data.removeRow(0);
					 
					 countRow = 0;
				 }
				 
				 String[][] dataFromFile = null;
				 // Чтение данных из файла выбранного расширения.
				 if (fileNameRead.endsWith(".txt")) 
					 dataFromFile = readTXT(dataFromFile, columnNames.length);
				 else 
					 dataFromFile = readXML(columnNames, nameElem, dataFromFile);
				 
				 // Перенос прочитанных данных в таблицу.
				 log.debug("Перенос данных из файла в таблицу.");
				 int countDelete = 0; 
				 
				 for (int i = 0; i < dataFromFile.length; i++) {
					 if (checkTable(checkColumn, data, dataFromFile[i])) {
						 createElemDatabase(monthNumb, dataFromFile[i], columnNames[0]);
						 data.addRow(dataFromFile[i]);
					 } else 
						 countDelete++;
				 }
				 
				 if (countDelete == 0)
					 JOptionPane.showMessageDialog(null,
					       		"Данные успешно загружены из файла " + fileNameRead,
					            "Успешно", JOptionPane.INFORMATION_MESSAGE,
					            new ImageIcon("./img/check.png"));
				 else
					 JOptionPane.showMessageDialog(null,
					       		"<html>Данные из файла " + fileNameRead  + " успешно загружены!<br />Осторожно:"
					       				+ "<br /> Некоторые строки  из файла повторяли элементы, уже существующие в списке,<br />"
					       				+ "поэтому не были добавлены.<br /> Количество таких строк: " + countDelete + "</html>",
					            "Предупреждение", JOptionPane.INFORMATION_MESSAGE,
					            new ImageIcon("./img/warning.png"));
				 

			 } catch (FileExtensionException ex) {
				log.warn("Пользователем неверно указано расширение файла.", ex);
				JOptionPane.showMessageDialog(null, ex.getMessage(),
						"Ошибка", JOptionPane.INFORMATION_MESSAGE,
						new ImageIcon("./img/warning.png"));
			 } catch (IndexOutOfBoundsException e) {
				 log.warn("Попытка загрузки сеанса с несуществующим в БД фильмом", e);
				 JOptionPane.showMessageDialog(null, "<html>Один из сеансов содержит несуществующий фильм '" + lastTitle + 
						 "'.<br />Добавьте данные об этом фильме в репертуар, после чего повторите попытку.</html>" ,
							"Ошибка", JOptionPane.INFORMATION_MESSAGE,
							new ImageIcon("./img/warning.png"));
			 } catch (DataFileException ex) {
				 log.warn("Пользователем выбрана неподходящая таблица.", ex);
				 JOptionPane.showMessageDialog(null, ex.getMessage(),
							"Ошибка", JOptionPane.INFORMATION_MESSAGE,
							new ImageIcon("./img/warning.png"));
			 } catch (FileNotFoundException ex) {
				 log.warn("Файл пользователя не был найден.", ex);
				 JOptionPane.showMessageDialog(null, "Указанный файл не был найден, повторите попытку!",
							"Ошибка", JOptionPane.INFORMATION_MESSAGE,
							new ImageIcon("./img/warning.png"));
			 } catch (IOException ex) {
				 log.warn("Ошибка при загрузке файла.", ex);
				 JOptionPane.showMessageDialog(null, "Возникла ошибка при попытке получения доступа к файлу, попробуйте еще раз!",
							"Ошибка", JOptionPane.INFORMATION_MESSAGE,
							new ImageIcon("./img/warning.png"));
			 } catch (ParserConfigurationException ex) {
				 log.warn("Ошибка при загрузке файла.", ex);
				 JOptionPane.showMessageDialog(null, "<html>При попытке создания парсера произошла ошибка!<br />"
				 		+ "Повторите попытку или обратитесь к разработчику за помощью с устранением ошибки.</html>",
							"Ошибка", JOptionPane.INFORMATION_MESSAGE,
							new ImageIcon("./img/warning.png"));
			 } catch (SAXException ex) {
				 log.warn("Ошибка при загрузке файла.", ex);
				 JOptionPane.showMessageDialog(null, "<html>Произошла ошибка при парсинге документа.<br />"
						 + "Повторите попытку или обратитесь к разработчику за помощью с устранением ошибки.</html>", 
							"Ошибка", JOptionPane.INFORMATION_MESSAGE,
							new ImageIcon("./img/warning.png"));
			 }
		 }
	}
	
	/**
	 * Функция проверки отсуствия существования элемента, аналогичного добавляемому, в таблице.
	 * @param checkColumn - проверяемые столбцы на совпадения.
	 * @param data - таблица.
	 * @param newData - добавляемый элемент.
	 * @return возвращает false, если такой элемент уже был в таблице; true, если такого элемента еще не встречалось.
	 */
	private boolean checkTable(int[] checkColumn, DefaultTableModel data, String[] newData) {
		// count - количество совпадающих искомых столбцов в строке, 
		// 		если совпадают все столбцы, обозначенные как те, которые совпадать не должны, то функция вернет false.
		int count;
		for(int i = 0; i < countRow; i++) {
			count = 0;
			for (int j = 0; j < checkColumn.length; j++) {
				if (data.getValueAt(i, checkColumn[j]).equals(newData[checkColumn[j]])) count++;
				if (count == checkColumn.length) return false;
			}
		}
		return true;
	}
	
	/**
	 * Создание элемента при загрузке данных в базе данных.
	 * @param flag - флаг, значащий ноль при загрузке списка фильмов, и не ноль при загрузке сеансов.
	 * @param values - массив значений, загружаемого элемента.
	 * @throws IndexOutOfBoundsException - ошибка, возникающая в случае, если пользователь пытается добавить сеанс с фильмом, которого нет в базе данных.
	 */
	private void createElemDatabase(int monthNumb, String[] values, String flag) throws IndexOutOfBoundsException {
		
		if (flag.equals("Name")) {
			Info_Film info = OperationsWithDatabase.convertInfo(values[1], values[2]);
			// дополнительная проверка, необходимая, тк в списке репертуара отображаются не все существующие фильмы, 
			// а checkTable проверяет только по самой таблице. С помощью этой проверки мы избегаем случая добавления дубликата в базу данных.
			if (OperationsWithDatabase.getMovie(values[0], Integer.valueOf(values[3]), values[4], info).size() == 0) {
				EntityManager em = Singleton.createEMandTrans();
				em.persist(new Film(values[0], Integer.valueOf(values[3]), values[4], info)); 
				Singleton.finishEMandTrans(em);
			}
		} else {
			EntityManager em = Singleton.createEMandTrans(); 
			lastTitle = values[2];
			Query qFilm = em.createQuery("SELECT u FROM Film u WHERE u.title = :title");
			qFilm.setParameter("title", values[2]);
			// 0 - тк мы достаем первый попавшийся фильм с подходящим названием.
			Film edFilm = (Film) qFilm.getResultList().get(0);
			Session neww = new Session(monthNumb, Integer.valueOf(values[0]), values[1], edFilm);
			em.persist(neww);
			neww.fill_tckts(em);
			Singleton.finishEMandTrans(em);
		}
	}
	
	/**
	 * Чтение .txt файла с загружаемым списком.
	 * @param result - заполняемый данными из таблицы массив строк. 
	 * @param countColumns - количество переносимых столбцов.
	 * @return Массив элементов таблицы из файла.
	 */
	private String[][] readTXT(String[][] result, int countColumns) throws DataFileException, FileNotFoundException, IOException {
		// Промежуточный массив заранее слишком большого размера, 
		// необходим для определения количества строк в таблице в файле.
		String[][] cash = new String[512][countColumns];
		log.debug("Открытие считывающего потока данных с файла.");
		BufferedReader reader = new BufferedReader(new FileReader(fileNameRead));
			
		// Считывание строк из таблицы.
		String stringFromFile = reader.readLine();
		int countElems = 0;
		while (stringFromFile != null) {
			String[] newElem = stringFromFile.split(";");
			if (newElem.length == countColumns) {
				cash[countElems++] = newElem; 
			} else {
				throw new DataFileException();
			}
			stringFromFile = reader.readLine();
		}
		log.debug("Закрытие потока чтения данных с файла.");
		reader.close();
			
		result = new String[countElems][countColumns + 1];
		for (int i = 0; i < countElems; i++) 
			result[i] = cash[i];
		return result;
	}
	
	/**
	 * Считывание таблицы из .xml файла.
	 * @param columnNames - названия столбцов на латинице.
	 * @param nameElem - тэг необходимых элементов в файле.
	 * @param result - массив строк из файла.
	 * @return Массив строк из файла для переноса в таблицу.
	 */
	private String[][] readXML(String[] columnNames, String nameElem, String[][] result) 
			throws DataFileException, ParserConfigurationException, SAXException, IOException {
		Document doc = null;
		// Создание парсера документа и чтение оного из файла.
		log.debug("Создание парсера документа, чтение из документа " + fileNameRead);
		DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		doc = dBuilder.parse(new File(fileNameRead));
		doc.getDocumentElement().normalize();
		
		// Получение списка элементов из файла, инициализация результирующего массива необходимого размера.
		NodeList listFile = doc.getElementsByTagName(nameElem);
		result = new String[listFile.getLength()][columnNames.length];
		for (int temp = 0; temp < listFile.getLength(); temp++) {
			
			// Выбор элемента списка, чтение его атрибутов, преобразование в строку и занесение в результ. массив.
			Node elem = listFile.item(temp);
			NamedNodeMap attrs = elem.getAttributes();
			
			List<String> elemsList = new ArrayList<String>();
			for (int j = 0; j < columnNames.length; j++) 
				elemsList.add(attrs.getNamedItem(columnNames[j]).getNodeValue());
			String[] elems = new String[elemsList.size()];
			elemsList.toArray(elems);
			if (elemsList.size() != columnNames.length) 
				throw new DataFileException();
			result[temp] = elems;
		}
		
		return result;
	}
	
	/** 
	 * Метод перехвата исключения неверного ввода расширения файла.
	 * @param fileNameSave - введенное имя файла.
	 * @throws MyExceptionFileName - исключение некорректного расширения.
	 */
	private void checkFileName(String fileNameSave) throws FileExtensionException {
		if (!fileNameSave.endsWith(".txt") && !fileNameSave.endsWith(".xml")) throw new FileExtensionException();
	}
	
	/**
	 * Класс исключения ошибки ввода имени файла с некорректным расширением.
	 */
	private class FileExtensionException extends Exception {
		public FileExtensionException() {
			super("<html>Выбрано некорректное расширение файла!<br />"
					+ "Обратите внимание, что к чтению доступны только файлы *.txt или *.xml.</html>");
		}
	}
	
	/**
	 * Исключение ошибки некорректно выбранного файла для обновления таблицы.
	 */
	private class DataFileException extends Exception {
		public DataFileException() {
			super("<html>Файл содержит некорректное количество столбцов.<br />"
					+ "Убедитесь, что загрузили нужный документ.</html>");
		}
	}
}
