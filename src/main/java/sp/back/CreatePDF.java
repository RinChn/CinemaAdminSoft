package sp.back;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Arrays;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.FileDialog;

import org.apache.log4j.Logger;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

/**
 * Класс генерации PDF-отчета списка.
 * @author Арина Морик
 */
public class CreatePDF extends JFrame {
	
	/** Имя выбранного для сохранения отчета файла **/
	private String fileName;
	
	/** Файл генерируемого отчета **/
	private Document fileForSave;
	/** Таблица в отчете **/
	private PdfPTable tableFile;
	
	/** Основа для шрифта текста ячеек таблицы **/
	private BaseFont bfComic;
	/** Основа для шрифта текста в названиях столбцов в таблице **/
	private BaseFont bfComicHeader;
	/** Шрифт текста ячеек таблицы **/
	private Font fontTable;
	/** Шрифт текста в названиях столбцов в таблице **/
	private Font fontHeader;
	
	/** Логгер класса **/ 
	private static final Logger log = Logger.getLogger(CreatePDF.class.getName());
	
	/**
	 * Конструктор класса.
	 * @param data - модель таблицы, данные из которой будут изыматься.
	 * @param nameList - название списка, включенного в отчет.
	 * @param ColumnNames - имена столбцов, которые будут перенесены в отчет.
	 * @param sizeColumn - размер столбцов.
	 * @param lastList - флаг, чье значение true, если список будет последним в файле, и false, если нет.
	 * @throws FileNotFoundException - исключение ошибки отсутствия файла.
	 * @throws DocumentException - исключение ошибки в документе.
	 */
	public CreatePDF(DefaultTableModel data, String nameList, String[] ColumnNames, float[] sizeColumn, 
			boolean lastList) throws DocumentException {
		// Подготовка документа и таблицы для генерации отчёта.
		fileForSave = new Document(PageSize.A4, 0, 0, 0, 0);
		tableFile = new PdfPTable(ColumnNames.length);
		tableFile.setWidths(sizeColumn);
		
		// Выбор пользователем файла для загрузки.
		FileDialog window = new FileDialog(this, "Выберите файл для генерации отчета", FileDialog.SAVE);
		window.setFile("*.pdf");
		window.setVisible(true);
		fileName = window.getDirectory() + window.getFile();
		
		if(window.getDirectory() != null) {
			log.info("Генерация отчета для таблицы " + nameList + " в файл " + fileName);
			try {
				
				checkFileName(fileName);
				createWriterFont(fileForSave);
				
				// Добавление в таблицу заголовков столбцов и содержания столбцов, взятые из модели data. 
				for (int i = 0; i < ColumnNames.length; i++)
					tableFile.addCell(new PdfPCell(new Phrase(ColumnNames[i], fontHeader)));
			
				for(int i = 0; i < data.getRowCount(); i++) {
					for (int j = 0; j < ColumnNames.length; j++)
						tableFile.addCell(new Phrase((String) data.getValueAt(i,j),fontTable));
				}
				
				// Открытие самого файла, заполнение его соответствующими элементами.
				fileForSave.open();
				try {
					Image image = Image.getInstance("img/head.png");
					fileForSave.add(image);
					fullFile(nameList, fileForSave);
				} catch (IOException e) {
					log.warn("Ошибка адреса изображения для шапки отчета.", e);
					e.printStackTrace();
				} 
				if (lastList) {
				    JOptionPane.showMessageDialog(this,
				    		"Отчет успешно сохранен в файл " + fileName,
				            "Успешно", JOptionPane.INFORMATION_MESSAGE,
				            new ImageIcon("./img/check.png"));
				    log.debug("Закрытие файла для записи.");
					// Закрытие потока.
					fileForSave.close();
				} 
				
			} catch (FileExtensionException ex) {
				log.warn("Ошибка указанного расширения файла.", ex);
				JOptionPane.showMessageDialog(null, ex.getMessage(),
	                    "Ошибка", JOptionPane.INFORMATION_MESSAGE,
	                    new ImageIcon("./img/warning.png"));
			}
		}
	}
	
	/**
	 * Конструктор для добавления еще одного списка в уже созданный файл.
	 * @param fileWithTable - документ, куда будет производиться сохранение.
	 * @param fileNameNeed - путь к файлу.
	 * @param data - модель, из которой считывается таблица. 
	 * @param nameList - название списка, заносящегося в отчет.
	 * @param ColumnNames - массив наименований столбцов таблицы.
	 * @param sizeColumn - размер столбцов.
	 * @throws FileNotFoundException - исключение ошибки отсутствия файла.
	 * @throws DocumentException - исключение ошибки в документе.
	 */
	public CreatePDF(Document fileWithTable, String fileNameNeed, DefaultTableModel data, 
			String nameList, String[] ColumnNames, float[] sizeColumn)
			throws FileNotFoundException, DocumentException {
		fileName = fileNameNeed;
		log.info("Генерация отчета для таблицы " + nameList + " в файл " + fileName);
		// Создание новой страницы в файле, подготовка таблицы.
		fileWithTable.newPage();
		tableFile = new PdfPTable(ColumnNames.length);
		tableFile.setWidths(sizeColumn);
		
		createWriterFont(fileWithTable);
				
		// Добавление в таблицу заголовков столбцов и содержания столбцов, взятые из модели data. 
		for (int i = 0; i < ColumnNames.length; i++)
			tableFile.addCell(new PdfPCell(new Phrase(ColumnNames[i], fontHeader)));
			
		for(int i = 0; i < data.getRowCount(); i++) {
			for (int j = 0; j < ColumnNames.length; j++)
				tableFile.addCell(new Phrase((String) data.getValueAt(i,j),fontTable));
		}
				
		// Открытие самого файла, заполнение его соответствующими элементами.
		fileWithTable.open();
		fullFile(nameList, fileWithTable);
		JOptionPane.showMessageDialog(this,
		    "Отчет успешно сохранен в файл " + fileName,
		    "Успешно", JOptionPane.INFORMATION_MESSAGE,
		    new ImageIcon("./img/check.png"));
		log.debug("Закрытие файла для записи.");
		// Закрытие потока.
		fileWithTable.close();
	}
	
	/**
	 * Метод открытия записывающего потока и создания баз для шрифтов и самих шрифтов.
	 * @param report - документ, куда будет сохранен список.
	 * @throws DocumentException 
	 * @throws FileNotFoundException 
	 */
	private void createWriterFont(Document report) throws DocumentException {
		
		log.debug("Открытие записывающего в файл потока.");
		
		// Объявление баз для шрифтов и самих шрифтов текста в таблице.
		try {
			// Открытие записывающего в файл потока.
			PdfWriter.getInstance(report, new FileOutputStream(fileName));
			log.debug("Инициализация баз для шрифтов.");
			bfComic = BaseFont.createFont("/Windows/Fonts/Arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			bfComicHeader = BaseFont.createFont("/Windows/Fonts/impact.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		} catch (IOException e) {
			log.error("Шрифты не были найдены.", e);
			e.printStackTrace();
		} 
		fontTable = new Font(bfComic, 10);
		fontHeader = new Font(bfComicHeader, 11);
		
	}
	
	/**
	 * Метод, возвращающий документ, куда была сохранена таблица.
	 * @return fileForSave - документ, куда была сохранена таблица.
	 */
	public Document getDocument() {
		log.info("Получение документа для сохранения отчета");
		return fileForSave;
	}
	
	/**
	 * Метод, возвращающий путь к файлу, куда была сохранена таблица.
	 * @return fileName - путь к файлу, куда была сохранена таблица.
	 */
	public String getDocName() {
		log.info("Получение имени документа для сохранения отчета");
		return fileName;
	}
	
	/**
	 * Метод генерации заголовка страницы.
	 * @param nameList - наименование таблицы в файле.
	 * @param fileForSave - документ, куда сохраняются заголовки.
	 * @throws DocumentException - исключение ошибки в документе.
	 */
	private void fullFile(String nameList, Document fileForSave) throws DocumentException {
		log.debug("Перенос в документ таблицы с данными.");
		char[] sepLine = new char[89];
	    Arrays.fill(sepLine, '_');
	    Paragraph titleTable = new Paragraph(nameList, FontFactory.getFont(FontFactory.COURIER_BOLD,
				20, Font.BOLD, BaseColor.BLACK));
		titleTable.setAlignment(Element.ALIGN_CENTER);
		
		fileForSave.add(new Paragraph(new String(sepLine) + "\n\n"));
		fileForSave.add(titleTable);
		fileForSave.add(new Paragraph(new String(sepLine) + "\n\n"));
		fileForSave.add(tableFile);

	}
	
	/**
	 * Исключение введения пользователем файла какого-либо иного расширения, кроме pdf.
	 */
	private class FileExtensionException extends Exception {
		public FileExtensionException() {
			super("<html>Выбрано некорректное расширение файла!<br />"
					+ "Обратите внимание, что для создания отчетов доступны только файлы *.pdf.</html>");
		}
	}
	
	/**
	 * Перехват ошибки некорректного выбора файла.
	 * @param fileNameSave - имя выбранного файла.
	 * @throws FileExtensionException - исключение неверного расширения файла.
	 */
	private void checkFileName(String fileNameSave) throws FileExtensionException {
		if (!fileNameSave.endsWith(".pdf")) throw new FileExtensionException();
	}
}
