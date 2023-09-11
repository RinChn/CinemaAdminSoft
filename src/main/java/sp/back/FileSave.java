package sp.back;

import java.awt.FileDialog;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** Сохранение таблицы в файл
 * @author Арина Морик
 */
public class FileSave extends JFrame {
	
	/** Всплывающее окно с выбором файла для сохранения списка **/
	private FileDialog window;
	/** Имя файла, выбранного для сохранения **/
	private String fileNameSave;
	/** Поток, записывающий данные из таблицы в файл **/
	private BufferedWriter writer;
	/** Количество строк в таблице **/
	private int countRow;
	/** Количество столбцов в таблице **/
	private int countColumn;
	/** Файл, куда производится сохранение. **/
	private Document doc;
	/** Логгер класса **/
	private static final Logger log = Logger.getLogger(FileSave.class.getName());
	
	/**
	 * Конструктор класса.
	 * @param windowName - название всплывающего окна с выбором файла.
	 * @param data - модель сохраняемой таблицы.
	 * @param columnNames - названия столбцов на латинице для сохранения в xml.
	 * @param nameList - название списка для сохранения в xml.
	 * @param nameElem - название элемента списка для сохранения в xml.
	 */
	public FileSave(String windowName, String[][] data, String[] columnNames, 
					String nameList, String nameElem) {
		
		// Открытие окна для выбора файла для записи.
		 window = new FileDialog(this, windowName, FileDialog.SAVE);
		 window.setVisible(true);
		 
		 // Получение имени файла.
		 fileNameSave = window.getDirectory() + window.getFile();
		 
		 // Проверяем на корректность выбранный файл (соответствие расширения), сохраняем список в нужный файл.
		 if(window.getDirectory() != null) {
			 log.info("Сохранение данных в файл " + fileNameSave + " из таблицы " + nameList);
			 try {
				 checkFileName(fileNameSave);
				 
				 countRow = data.length;
				 countColumn = columnNames.length;
				 
				 if (fileNameSave.endsWith(".txt")) saveTXT(data);
				 else saveXML(data, columnNames, nameList, nameElem);
				 
				JOptionPane.showMessageDialog(this,
						"Данные успешно сохранены в файл " + fileNameSave,
						"Успешно", JOptionPane.INFORMATION_MESSAGE,
						new ImageIcon("./img/check.png"));

			 } catch (FileNameException ex) {
				 log.warn("Пользователем неверно указано расширение файла.", ex);
				 JOptionPane.showMessageDialog(null, ex.getMessage(),
		                   "Ошибка", JOptionPane.INFORMATION_MESSAGE,
		                   new ImageIcon("./img/warning.png"));
			 } catch (IOException ex) {
				 log.warn("Ошибка при сохранении файла.", ex);
				 JOptionPane.showMessageDialog(null, "Возникла ошибка при попытке получения доступа к файлу, попробуйте еще раз!",
							"Ошибка", JOptionPane.INFORMATION_MESSAGE,
							new ImageIcon("./img/warning.png"));
			 } catch (ParserConfigurationException ex) {
				 log.warn("Ошибка при сохранении файла.", ex);
				 JOptionPane.showMessageDialog(null, "<html>При попытке создания парсера произошла ошибка!<br />"
					 		+ "Повторите попытку или обратитесь к разработчику за помощью с устранением ошибки.</html>",
								"Ошибка", JOptionPane.INFORMATION_MESSAGE,
								new ImageIcon("./img/warning.png"));
			 } catch (TransformerConfigurationException ex) {
				 log.warn("Ошибка при сохранении файла.", ex);
				 JOptionPane.showMessageDialog(null, "<html>Возникла проблема с получением доступа к XML-файлу.<br />"
				 		+ "Попробуйте другой документ или обратитесь к разработчику за помощью.</html>",
								"Ошибка", JOptionPane.INFORMATION_MESSAGE,
								new ImageIcon("./img/warning.png"));
			 } catch (TransformerException ex) {
				 log.warn("Ошибка при сохранении файла.", ex);
				 JOptionPane.showMessageDialog(null, "<html>XML-файл некорректен.<br />"
					 		+ "Попробуйте другой документ или обратитесь к разработчику за помощью.</html>",
									"Ошибка", JOptionPane.INFORMATION_MESSAGE,
									new ImageIcon("./img/warning.png"));
			 }
		 }
	}

	/**
	 * Сохранение списка в файл .txt.
	 * @param data - модель сохраняемой таблицы.
	 */
	private void saveTXT(String[][] data) throws IOException {
		log.debug("Открытие записывающего потока.");
		writer = new BufferedWriter(new FileWriter(fileNameSave));
			
		log.debug("Запись данных в файл");
		for(int i = 0; i < countRow; i++) {
			for(int j = 0; j < countColumn - 1; j++) 
				writer.write((String)data[i][j] + ";");
			writer.write((String)data[i][countColumn - 1]);
			writer.write("\r\n");
		}
		log.debug("Закрытие записывающего потока");
		writer.close();
	}
	
	/**
	 * Сохранение списка в файл xml.
	 * @param data - модель сохраняемой таблицы.
	 * @param columnNames - названия столбцов на латинице.
	 * @param nameList - название списка. 
	 * @param nameElem - название элемента списка.
	 */
	private void saveXML(String[][] data, String[] columnNames, String nameList, String nameElem) 
			throws ParserConfigurationException, TransformerConfigurationException, TransformerException, IOException {
		
		// Создание парсера и файла.
		log.debug("Создание парсера документа.");
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		doc = builder.newDocument();
		
		// Создание корневого элемента и последующее добавление элементов таблицы в файл
		Node fileList = doc.createElement(nameList);
		doc.appendChild(fileList);
		for (int i = 0; i < countRow; i++)
		{
			Element elem = doc.createElement(nameElem);
			fileList.appendChild(elem);
			for (int j = 0; j < countColumn; j++) {
				elem.setAttribute(columnNames[j], (String)data[i][j]);
			}
		}
		
		// Сохранение полученного списка в файл.
		log.debug("Запись данных в файл, открытие записывающего потока.");
		Transformer trans = TransformerFactory.newInstance().newTransformer();
		java.io.FileWriter fw = new FileWriter(fileNameSave);
		trans.transform(new DOMSource(doc), new StreamResult(fw));
		log.debug("Закрытие записывающего потока.");
		fw.close();
	}
	
	/**
	 * Перехват ошибки некорректного выбора файла.
	 * @param fileNameSave - имя выбранного файла.
	 * @throws FileNameException - исключение неверного расширения файла.
	 */
	private void checkFileName(String fileNameSave) throws FileNameException {
		if (!fileNameSave.endsWith(".txt") && !fileNameSave.endsWith(".xml")) throw new FileNameException();
	}
	
	/**
	 * Исключение введения пользователем файла какого-либо иного расширения, кроме xml и txt.
	 */
	private class FileNameException extends Exception {
		public FileNameException() {
			super("<html>Выбрано некорректное расширение файла!<br />"
					+ "Обратите внимание, что к сохранению доступны только файлы *.txt или *.xml.</html>");
		}
	}
	
}

