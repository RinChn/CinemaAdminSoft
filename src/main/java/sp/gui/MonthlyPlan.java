package sp.gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.util.regex.*;
import java.util.stream.Collectors;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import com.itextpdf.text.DocumentException;

import jakarta.persistence.EntityManagerFactory;

import sp.back.*;
import sp.entities.*;


/**
 * Класс экранной формы расписания сеансов на месяц.
 * @author Арина Морик
 * @version 1.0
 */
public class MonthlyPlan {
	
	/** Фабрика интерфейса для работы с JPA **/
	private static final EntityManagerFactory emf = Singleton.getInstance().getEntityManagerFactory();
	/** Номер месяца, расписание сеансов на который высвечивается **/
	private int monthNumb;	
	/** Репертуар на открытый месяц **/
	private Repertoire monthlyRep;
	/** Список уникальных наименований фильмов, демонстрируемых в открытом месяце. **/
	private List<Film> allFilms = new ArrayList<Film>();
	/** Контейнер, содержащий в себе все элементы экранной формы **/
	private JFrame MonthlyPlan;
	
	/** Кнопка переключения на следующий месяц **/
	public JButton next;
	/** Кнопка переключения на предыдущий месяц **/
	public JButton previous;
	
	/** Кнопка сохранения списка в файл **/
	private JButton save;
	/** Кнопка загрузки списка из файла **/
	private JButton load;
	/** Кнопка открытия справки к программе. **/
	private JButton info;
	/** Кнопка добавления нового элемента в список **/
	private JButton add;
	/** Кнопка генерации отчёта **/
	private JButton createReport;
	/** Кнопка перехода на экранную форму репертуара**/
	private JButton repertoire;
	
	/** Модель таблицы **/
	private DefaultTableModel modelSessions;
	/** Таблица сеансов **/
	private JTable sessionsMonth;
	/** Массив названий столбцов таблицы **/
	private String[] columnNames;
	/** Массив значений строк таблицы **/
	private String[][] dataValues;
	
	/** Кнопка поиска **/
	private JButton filter;
	/** Текстовое поле поиска **/
	private JTextField search;
	/** Выпадающий список доступных столбцов для поиска **/
	private JComboBox<String> listOfColumns;
	/** Фильтрация таблицы по запросу **/
	private TableRowSorter<TableModel> sorter;
	
	/** Текстовое поле ввода даты сеанса при добавлении нового элемента в таблицу **/
	private JTextField newDate;
	/** Текстовое поле ввода времени сеанса при добавлении нового элемента в таблицу **/
	private JTextField newTime;
	/** Выпадающий список фильмов из репертуара на месяц **/
	private JComboBox<String> allFilmsChoice;
	
	/** Строка выбора списка репертуара для генерации отчета **/
	private JCheckBox reportMonthlyPlan;
	/** Строка выбора списка сеанслв для генерации отчета **/
	private JCheckBox reportRepertoire;
	
	/** Логгер класса **/
	private static final Logger log = Logger.getLogger(MonthlyPlan.class.getName());
	
	/**
	 * Конструктор класса. 
	 */
	public MonthlyPlan(int monthNumb) {
		
		this.monthNumb = monthNumb;
		
		// создание и форматирование экранной формы.
		MonthlyPlan = new JFrame("Кинотеатр");
		MonthlyPlan.setSize(700, 750);
		MonthlyPlan.setLocationRelativeTo(null);
		MonthlyPlan.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MonthlyPlan.setIconImage(new ImageIcon("./img/mediaplayer.png").getImage());

		// создание верхней панели с информацией о месяце открытого расписания.
		JPanel monthInfo = new JPanel();
		monthInfo.setBackground(java.awt.Color.darkGray);
		monthInfo.setLayout(new BorderLayout());
		JLabel monthName = new JLabel(HelperMonth.getName(monthNumb));
		monthName.setForeground(Color.white);
		monthName.setFont(new Font("Arial", Font.BOLD, 25));
		monthName.setHorizontalAlignment(JLabel.CENTER);
		previous = new JButton(new ImageIcon("./img/back.png"));
		previous.setForeground(Color.black);
		next = new JButton(new ImageIcon("./img/forward.png"));
		next.setForeground(Color.black);
		monthInfo.add(monthName, BorderLayout.CENTER);
		monthInfo.add(previous, BorderLayout.WEST);
		monthInfo.add(next, BorderLayout.EAST);
		
		// создание панели инструментов.
		save = new JButton(new ImageIcon("./img/save.png"));
		info = new JButton(new ImageIcon("./img/information-toolbar.png"));
		load = new JButton(new ImageIcon("./img/download.png"));;
		repertoire = new JButton(new ImageIcon("./img/film.png"));
		createReport = new JButton(new ImageIcon("./img/pdf.png"));
		add = new JButton(new ImageIcon("./img/add.png"));
		save.setToolTipText("Сохранить в файл");
		load.setToolTipText("Загрузить новые данные");
		info.setToolTipText("Открыть справку о программе");
		createReport.setToolTipText("Генерировать PDF-отчет по списку");
		repertoire.setToolTipText("Открыть репертуар фильмов");
		add.setToolTipText("Добавить сеанс");
		JToolBar toolBar = new JToolBar();
		toolBar.setBounds(100, 100, 300, 300);
		toolBar.add(repertoire);
		toolBar.add(add);
		toolBar.add(save);
		toolBar.add(load);
		toolBar.add(createReport);
		toolBar.add(info);
		
		// создание главной панели, в которую включены панель с информацией о месяце и панель инструментов.
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(monthInfo, BorderLayout.NORTH);
		mainPanel.add(toolBar, BorderLayout.SOUTH);
		
		// добавление компановщика, добавление главной панели на экранную форму.
		MonthlyPlan.setLayout(new BorderLayout());
		MonthlyPlan.add(mainPanel, BorderLayout.NORTH);
		
		// создание таблицы с информацией о сеансах.
		columnNames = new String[]{"День", "Время", "Название фильма", ""};
		getListFromDatabase();
		modelSessions = new DefaultTableModel(dataValues, columnNames) {
			@Override
			public boolean isCellEditable(int row, int col) {
				return col == 3;
			}
		};
		sessionsMonth = new JTable(modelSessions);
		sessionsMonth.getTableHeader().setReorderingAllowed(false);
		sessionsMonth.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
		
		JScrollPane scroll = new JScrollPane(sessionsMonth);
		sessionsMonth.getColumn("").setCellRenderer(new ButtonRenderer());
		sessionsMonth.getColumn("").setCellEditor(new ButtonEditor(new JCheckBox()));
		sessionsMonth.setFont(new Font("Arial", Font.PLAIN, 12));
		MonthlyPlan.add(scroll, BorderLayout.CENTER);
		
		// создание списка столбцов, по которым может осуществляться поиск.
		listOfColumns = new JComboBox<String>(new String[]{"Столбец", "День", "Время", "Название фильма"});
		search = new JTextField();
		search.setColumns(30);
		filter = new JButton("Поиск");
		filter.setFont(new Font("Arial", Font.BOLD, 13));
		
		// создание сортировщика для фильтрации списка по запросу пользователя.
		sorter = new TableRowSorter<TableModel>(modelSessions);
		sessionsMonth.setRowSorter(sorter);
				
		// компановка и объединение элементов поиска в общую панель, добавление панели на экранную форму.
		JPanel filterPanel = new JPanel();
		filterPanel.setBackground(java.awt.Color.gray);
		filterPanel.add(listOfColumns);
		filterPanel.add(search);
		filterPanel.add(filter);
		MonthlyPlan.add(filterPanel, BorderLayout.SOUTH);
		
		// Добавление слушателей кнопкам.
		addListener();
		
		// Создание экрана репертуара для этого месяца.
		monthlyRep = new Repertoire(this);
	}
	
	/**
	 * Метод демонстрации экранной формы.
	 */
	public void show() {
		log.info("Открытие экранной формы");
		MonthlyPlan.setSize(700, 750);
		MonthlyPlan.setLocationRelativeTo(null);
		MonthlyPlan.setVisible(true);
	}
	
	/**
	 * Геттер модели таблицы экранной формы.
	 * @return modelSessions - модель таблицы с сеансами.
	 */
	public DefaultTableModel getModel() {
		log.info("Получение модели таблицы расписания сеансов на месяц.");
		return modelSessions;
	}
	
	/** 
	 * Метод получения номера месяца.
	 * @return monthNumb - номер месяца.
	 */
	public int getMonthNumb() {
		return monthNumb; 
	}
	
	/**
	 * Геттер получения полного списка уникальных наименований фильмов, сеансы которых присутствуют в открытом месяце.
	 * @return массив фильмов, составляющих репертуар. 
	 */
	public Film[] getListOfFilms() {
		return allFilms.toArray(new Film[allFilms.size()]);
	} 
	
	/**
	 * Геттер получения полного списка уникальных наименований фильмов, сеансы которых присутствуют в открытом месяце в формате List<Film>.
	 * @return список фильмов, составляющих репертуар. 
	 */
	public List<Film> getArrayOfFilms() {
		return allFilms;
	}
	
	/**
	 * Геттер получения координат выбранного в таблице элемента. 
	 * @return координаты выбранного в таблице элемента.
	 */
	public int[] getSelectedItemID() {
		return new int[] {sessionsMonth.convertRowIndexToModel(sessionsMonth.getSelectedRow()), 
				sessionsMonth.getSelectedColumn()};
	}
	
	/**
	 * Метод получения фрейма.
	 * @return MonthlyPlan
	 */
	public JFrame getFrame() {
		return MonthlyPlan;
	}
	
	/**
	 * Метод скрытия экранной формы.
	 */
	public void close() {
		log.info("Закрытие экранной формы");
		MonthlyPlan.setVisible(false);
	}
	
	/**
	 * Метод добавления нового фильма в список уникальных наименований фильмов.
	 * @param addFilm - название нового фильма.
	 */
	public void addNewFilmList(Film addFilm) {
		allFilms.add(addFilm);
		allFilms = new ArrayList<>(new HashSet<>(allFilms));
	}
	
	/** 
	 * Удаление фильма из списка всех фильмов.
	 * @param film - название удаляемого фильма.
	 */
	public void deleteFilmList(Film film) {
		for (int i = 0; i < allFilms.size(); i++) {
			if (allFilms.get(i).equals(film)) {
				allFilms.remove(i);
				break;
			}
		}
	}
	
	/**
	 * Получение списка сеансов из базы данных.
	 */
	private void getListFromDatabase() {
		log.info("Загрузка сеансов из базы данных");
		
		// получение списка из бд
		List<Session> allSessions = OperationsWithDatabase.getSortSessionsMonth(monthNumb);
		ArrayList<String[]> values = new ArrayList<String[]>(allSessions.size() + 1);
		
		for (int i = 0; i < allSessions.size(); i++) {
			Session entity = allSessions.get(i);
			values.add(new String[]{String.valueOf(entity.getDate()), entity.getTime(), entity.getFilm().getTitle()});
		}
        
        // Перевод в нужный формат для заполнения таблицы данными.
		dataValues = new String[values.size()][3];
		for (int i = 0; i < values.size(); i++)
			dataValues[i] = values.get(i);
		
		// создание списка уникальных фильмов, участвующих в сеансах в этом месяце
		List<Film> movies = allSessions.stream().map(Session::getFilm).collect(Collectors.toList());
		allFilms = new ArrayList<>(new HashSet<>(movies));
	}
	
	/**
	 * Метод добавления слушателей кнопкам.
	 */
	private void addListener() {
		
		createReport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// Вывод окна с выбором списков
				int userChoiceReport = showWindowChoiceReport();
				boolean choiceMP = reportMonthlyPlan.isSelected();
				boolean choiceRep = reportRepertoire.isSelected();
				if (userChoiceReport == JOptionPane.YES_OPTION) {
					try {
						// Проверка на присутствие выбора, 
						// генерация отчета по выбранным спискам, если выбор был совершен.
						checkChoiceReport(choiceMP, choiceRep);
						loadListsPDF(choiceMP, choiceRep);
					} catch(ChoiceReportException ex) {
						log.warn("Пользователь не выбрал список для генерации PDF: ", ex);
						JOptionPane.showMessageDialog(null, ex.getMessage(),
								"Ошибка", JOptionPane.INFORMATION_MESSAGE,
								new ImageIcon("./img/warning.png"));
					} 
				}
			}
		});
		
		// Кнопка перехода на экран репертуара.
		repertoire.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				log.info("Переход с главной экранной формы на экранную форму репертуара.");
				close();
				monthlyRep.show();
			}
		});
		
		// Кнопка Поиска.
		filter.addActionListener(new ActionListener() {
	          public void actionPerformed(ActionEvent e) {
	        	  log.debug("Нажатие кнопки 'поиск'.");
	        	  String text = search.getText();
	        	  try {
	        		  Object selected = listOfColumns.getSelectedItem();
	        		  checkSearchField(selected.toString());
	            	
	        		  int col = -1;
	        		  for (int i = 0; i < 4 && col == -1; i++) 
	        			  if (columnNames[i].equals(selected)) col = i;
	        		  
		            
	        		  try {
	        			  log.info("Поиск по таблице по запросу '" + text + "' в столбце " + selected.toString());
	        			  sorter.setRowFilter(RowFilter.regexFilter(text, col));
	        		  } catch(PatternSyntaxException pse) {
	        			  log.warn("Попытка сортировки по некорректному регулярному выражению.", pse);
	        			  System.err.println("Bad regex pattern");
	        		  }
		            
	        	  } catch(MissSelectSearchException ex) { 
	        		  log.warn("При нажатии на кнопку 'поиск' не выбран столбец", ex);
	        		  JOptionPane.showMessageDialog(MonthlyPlan,
		                      	ex.getMessage(),
		                      	"Ошибка", JOptionPane.INFORMATION_MESSAGE,
		                      	new ImageIcon("./img/warning.png"));
	        	  }
	          }
	     });
		
		// Кнопка сохранения в файл.
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				log.debug("Нажатие кнопки 'Сохранить таблицу'.");
				String[][] dataArray = new String[modelSessions.getRowCount()][modelSessions.getColumnCount()];
				for (int i = 0; i < modelSessions.getRowCount(); i++) {
					for (int j = 0; j < modelSessions.getColumnCount();j++) {
						dataArray[i][j] = (String) modelSessions.getValueAt(i, j);
					}
				}
				new FileSave("Выбор файла для сохранения", dataArray, 
						new String[] {"Day", "Time", "NameFilm"}, 
						"sessionsList_" + HelperMonth.getEnglishName(monthNumb), "session");
			}
		});
		
		// Кнопка загрузки из файла.
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				log.debug("Нажатие кнопки 'Загрузить таблицу'.");
				Object[] options = {"Дополнить",
	                    "Обновить",
	                    "Отмена"};
				int mode = JOptionPane.showOptionDialog(MonthlyPlan, "Выберите режим загрузки данных в список", "Загрузка данных",
				    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon("./img/choice.png"), options, null);
				
				if (mode != JOptionPane.CANCEL_OPTION && mode != JOptionPane.CLOSED_OPTION) {
					new FileRead("Выбор файла для загрузки", modelSessions, new int[] {mode, monthNumb}, 
						new int[] {0, 1, 2}, new String[] {"Day", "Time", "NameFilm"}, "session");
					updateAllListsFilms();
				}
			}
		});
		
		// Кнопка вывода справки.
		info.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				outputInfo();
			}
		});
		
		// Кнопка добавления элемента.
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					int result = addNewElem();
					if (result == JOptionPane.OK_OPTION) {
						String strDate = newDate.getText();
						String strTime = newTime.getText();
						checkAdd(strDate, strTime, allFilmsChoice.getSelectedItem().toString());
						log.info("Добавление нового сеанса " + strDate + "марта в "+ " " + strTime + " в таблицу");
						addNewElemInDatabase(strDate, strTime);
						((DefaultTableModel)sessionsMonth.getModel()).addRow(new Object[]{strDate, strTime, 
								allFilmsChoice.getSelectedItem()});
					}
				} catch (RepeatAddException ex) {
					log.warn("Попытка добавления уже существующего элемента в таблицу.", ex);
					JOptionPane.showMessageDialog(MonthlyPlan, ex.getMessage(),
	                    "Ошибка", JOptionPane.INFORMATION_MESSAGE,
	                    new ImageIcon("./img/warning.png"));
				} catch (WrongDayException ex) { 
					log.warn("Попытка добавления элемента с некорректно заполненным полем даты.", ex);
					JOptionPane.showMessageDialog(MonthlyPlan, ex.getMessage(),
		                    "Ошибка", JOptionPane.INFORMATION_MESSAGE,
		                    new ImageIcon("./img/warning.png"));
				} catch (EmptyFieldException ex) {
					log.warn("Попытка добавления элемента с пустым значением поля.", ex);
					JOptionPane.showMessageDialog(MonthlyPlan, ex.getMessage(),
	                    "Ошибка", JOptionPane.INFORMATION_MESSAGE,
	                    new ImageIcon("./img/warning.png"));
				} catch (NullPointerException ex) {
					log.warn("Попытка добавления элемента с пустым значением поля 'Фильм'.", ex);
					JOptionPane.showMessageDialog(MonthlyPlan, "<html>При создании не выбран фильм.<br />"
							+ "Убедитесь, что репертуар на этот месяц не пуст.</html>",
	                    "Ошибка", JOptionPane.INFORMATION_MESSAGE,
	                    new ImageIcon("./img/warning.png"));
				} catch (WrongTimeException ex) {
					log.warn("Попытка добавления элемента с некорректно заполненным полем времени.", ex);
					JOptionPane.showMessageDialog(MonthlyPlan, ex.getMessage(),
		                    "Ошибка", JOptionPane.INFORMATION_MESSAGE,
		                    new ImageIcon("./img/warning.png"));
				}
			}
		});
		
	}
	
	/**
	 * Метод вывода справки о программе.
	 */
	private void outputInfo() {
		
		String reference = "<html><b>Уважаемый пользователь,</b><br />"
				+ "&nbsp;&nbsp;данный программный комплекс спроектирован в качестве<br />"
				+ "инструмента администрирования кинотеатром.<br />"
				+ "&nbsp;&nbsp;Дальнейшее руководство может помочь Вам при появлении<br />"
				+ "затруднений в процессе работы с программой.<br />"
				+ "<br /><b>- Сеансы:</b><br />"
				+ "&nbsp;&nbsp;Для переключения между месяцами необходимо нажимать<br />"
				+ "на соответствующие стрелки у названия месяца<br />"
				+ "в верхней части экрана.<br />"
				+ "&nbsp;&nbsp;При добавлении сеанса предоставляется список доступных<br />"
				+ "для показа фильмов. Если нужного Вам фильма в этом<br />"
				+ "перечне не оказалось, добавьте его, перейдя на экран<br />"
				+ "репертуара кликом на иконку с кинолентой в панели<br />"
				+ "инструментов наверху, тогда фильм автоматически<br />"
				+ "станет доступен для добавления в информацию о сеансе. <br />"
				+ "&nbsp;&nbsp;ВАЖНО: список фильмов в репертуаре обновляется при<br />"
				+ "перезапуске программы и переключении между месяцами.<br />"
				+ "&nbsp;&nbsp;Если фильм не участвует ни в одном сеансе, то он<br />"
				+ "автоматически перестанет отображаться в репертуаре.<br />"
				+ "Чтобы этого избежать, назначьте фильму хотя бы один<br />"
				+ "сеанс в нужный месяц.<br />"
				+ "&nbsp;&nbsp;Узнать подробности о демонстрируемом фильме,<br />"
				+ "забронировать билет или убрать с него бронь, а также<br />"
				+ "узнать количество купленных и свободных мест, можно<br />"
				+ "перейдя по кнопке \"Подробнее\" в строке с интересующим<br />"
				+ "Вас сеансом. Там же можно его удалить или изменить.<br />"
				+ "&nbsp;&nbsp;Для поиска выберите в списке рядом с полем для ввода<br />"
				+ "нужный столбец, по которому будет осуществляться поиск,<br />"
				+ "и введите подстроку, которая должна быть в интересующем<br />"
				+ "Вас элементе. <br />"
				+ "&nbsp;&nbsp;Отсортировать таблицу можно кликом на заголовок столбца,<br />"
				+ "по которому Вы хотите провести сортировку. Двойной<br />"
				+ "клик изменит направление сортировки.<br />"
				+ "<br />"
				+ "<b>- Репертуар:</b><br />"
				+ "&nbsp;&nbsp;Чтобы удалить фильм из репертуара этого месяца,<br />"
				+ "а следовательно удалить и все сеансы, где он показывался,<br />"
				+ "необходимо выделить строку с нужным фильмом, нажать пкм и<br />"
				+ "в выпавшем меню кнопку \"Удалить\". Чтобы удалить фильм<br />"
				+ "из базы данных, а следовательно и сеансы с ним за все<br />"
				+ "месяцы при их [сеансов] наличии, нужно нажать на кнопку<br />"
				+ "добавления фильма и выбрать \"Уже существующий фильм\",<br />"
				+ "Вам откроется полный список всех фильмов, каждый<br />"
				+ "из которых Вы можете при необходимости удалить,<br />"
				+ "проследовав тем же инструкциям, что и при удалении<br />"
				+ "фильма из репертуара.<br />"
				+ "&nbsp;&nbsp;Поиск работает так же, как и на экране сеансов.<br />"
				+ "&nbsp;&nbsp;При сохранении отчета с этой экранной формы будет<br />"
				+ "сохранен только репертуар на этот месяц.<br />"
				+ "<br />"
				+ "<b>- Прочее:</b><br />"
				+ "&nbsp;&nbsp;Все даты вводятся в формате **.**.****<br />"
				+ "&nbsp;&nbsp;Все время вводится в формате **:**<br />"
				+ "&nbsp;&nbsp;Сохранение и загрузка данных доступны<br />"
				+ "только для файлов .txt и .xml.<br />"
				+ "</html>";
        
        JLabel labelWithReference= new JLabel();
        labelWithReference.setFont(new Font("Arial", Font.PLAIN, 13));
        labelWithReference.setText(reference);
        
        JScrollPane scrollPane = new JScrollPane(labelWithReference);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        scrollPane.setPreferredSize(new Dimension(400, 450));

        JOptionPane.showMessageDialog(null, scrollPane, 
        		"Справка о программе", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("./img/information.png"));
	}
	
	/**
	 * Обновление уникальных фильмов.
	 */
	private void updateAllListsFilms() {
		List<Session> allSess = OperationsWithDatabase.getSortSessionsMonth(monthNumb);
		List<Film> movies = allSess.stream().map(Session::getFilm).collect(Collectors.toList());
		allFilms = new ArrayList<>(new HashSet<>(movies));
		monthlyRep = new Repertoire(this);
	}
	
	/** 
	 * Метод добавления нового сеанса в бд.
	 * @param strDate - дата проведения сеанса.
	 * @param strTime - время проведения сеанса.
	 */
	private void addNewElemInDatabase(String strDate, String strTime) {
		String titleFilm = allFilmsChoice.getSelectedItem().toString();
		Film addingFilm = new Film();
		for (Film movie : allFilms) {
			if (movie.getTitle().equals(titleFilm)) {
				addingFilm = movie;
				break;
			}
		}
		OperationsWithDatabase.createSessions(monthNumb, Integer.valueOf(strDate), strTime, addingFilm);
	}
	
	/**
	 * Метод создания всплывающей формы заполнения для добавления нового элемента в список.
	 * @return result - выбор пользователя в окне
	 */
	private int addNewElem() {
		JPanel infoNewSession = new JPanel(new GridLayout(3, 3));
		
		infoNewSession.add(new JLabel("Дата сеанса: ", 10));
		newDate = new JTextField(10);
		infoNewSession.add(newDate);
		
		infoNewSession.add(new JLabel("Время показа: ", 10));
		newTime = new JTextField(10);
		infoNewSession.add(newTime);
		
		infoNewSession.add(new JLabel("Фильм: ", 10));
		List<String> movies = allFilms.stream().map(Film::getTitle).collect(Collectors.toList());
		movies = new ArrayList<>(new HashSet<>(movies));
		String[] titlesFilms = (String[]) movies.toArray(new String[movies.size()]);
		Arrays.sort(titlesFilms);
		allFilmsChoice = new JComboBox<String>(titlesFilms);
		
		infoNewSession.add(allFilmsChoice);
		
		int result = JOptionPane.showOptionDialog(MonthlyPlan, infoNewSession, "Создание нового объекта", 
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, 
				new ImageIcon("./img/information.png"), null, null);
		return result;
	}
	
	/** 
	 * Метод демонстрации окна, где можно отметить, какие списки будут присутствовать в отчете.
	 * @return userChoiceReport - выбор пользователя в окне.
	 */
	private int showWindowChoiceReport() {
		reportMonthlyPlan = new JCheckBox("Список сеансов в этот месяц.");
		reportRepertoire = new JCheckBox("Репертуар на месяц.");
		int userChoiceReport = JOptionPane.showConfirmDialog(null,  
				new Object[]{"Выберите список(-ки) для генерации отчёта:", 
						reportMonthlyPlan, reportRepertoire}, "Выбор списка", 
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, 
				new ImageIcon("./img/choice.png")); 
		return userChoiceReport;
	}
	
	/**
	 * Метод, отвечающий за последовательное размещение списков в генерируемом отчете. 
	 * @param choiceMP - выбор, касаемо присутствия в отчете списка с расписанием сеансов на месяц.
	 * @param choiceRep - выбор, касаемо присутствия в отчете списка с репертуаром на месяц.
	 */
	private void loadListsPDF(boolean choiceMP, boolean choiceRep) {
		if (choiceMP && choiceRep) {
			try {
				CreatePDF report = new CreatePDF(modelSessions, "Sessions in " + HelperMonth.getEnglishName(monthNumb), 
						new String[]{"День", "Время", "Название фильма"},
						new float[]{20f, 20f, 38f}, false);
					
				new CreatePDF(report.getDocument(), report.getDocName(),
						monthlyRep.getModel(), "Repertoire for " + HelperMonth.getEnglishName(monthNumb), 
							new String[]{"Название фильма", "Режиссер", 
									"Жанр", "Длительность (мин)", "Дата премьеры"},
							new float[]{38f, 38f, 25f, 30f, 22f});
			} catch (FileNotFoundException ex)  {
				log.warn("Ошибка при загрузке данных", ex);
				JOptionPane.showMessageDialog(null, "<html>При сохранении списка возникла ошибка с доступом к файлу.<br />"
						+ "Повторите попытку или обратитесь к разработчику за помощью.</html>",
						"Ошибка", JOptionPane.INFORMATION_MESSAGE,
						new ImageIcon("./img/warning.png"));
			} catch (DocumentException ex) {
				log.warn("Ошибка при загрузке данных", ex);
				JOptionPane.showMessageDialog(null, "<html>Возникла проблема при генерации списка, повторите попытку.<br />"
						+ "В случае повторения проблемы обратитесь к разработчику.</html>",
						"Ошибка", JOptionPane.INFORMATION_MESSAGE,
						new ImageIcon("./img/warning.png"));
			}
				
		} else {
			try {
				if (choiceRep)
					new CreatePDF(monthlyRep.getModel(), "Repertoire for " + HelperMonth.getEnglishName(monthNumb), 
							new String[]{"Название фильма", "Режиссер", "Жанр", 
									"Длительность (мин)", "Дата премьеры"},
							new float[]{38f, 38f, 25f, 30f, 22f}, true);
				else 
					new CreatePDF(modelSessions, "Sessions in " + HelperMonth.getEnglishName(monthNumb), 
							new String[]{"День", "Время", "Название фильма"},
							new float[]{20f, 20f, 38f}, true);
			} catch (DocumentException ex) {
				log.warn("Ошибка при загрузке данных", ex);
				JOptionPane.showMessageDialog(null, "<html>Возникла проблема при генерации списка, повторите попытку.<br />"
						+ "В случае повторения проблемы обратитесь к разработчику.</html>",
						"Ошибка", JOptionPane.INFORMATION_MESSAGE,
						new ImageIcon("./img/warning.png"));
			}
		}
	}
	
	/**
	 * Класс визуализации кнопки в таблице.
	 */
	class ButtonRenderer extends JButton implements TableCellRenderer {
		
		public ButtonRenderer() { }
		/**
		 * Вид при нажатии.
		 */
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			setText((value == null) ? "Подробно" : value.toString());
			return this;
		}
	}

	/**
	 * Класс реакции для ячейки таблицы с кнопкой.
	 */
	class ButtonEditor extends DefaultCellEditor {
		/** Надпись на кнопке **/
		private String label;
		public ButtonEditor(JCheckBox checkBox) {
			super(checkBox);
		}
		/**
		 * Реакция на нажатие.
		 */
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
				label = (value == null) ? "Подробно" : value.toString();
				JButton detail = new JButton();
				detail.setText("Подробно");
				detail.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						actionDetail();
					}
				});
				return detail;
		}
		
		/**
		 * Вид после нажатия.
		 */
		public Object getCellEditorValue() {
			return new String(label);
		}
	}
	
	/**
	 * Метод открытия "подробнее" для сеанса.
	 */
	private void actionDetail() {
		int rowIndex = sessionsMonth.convertRowIndexToModel(sessionsMonth.getSelectedRow()); 
		log.debug("Нажатие кнопки 'подробнее' для строки " + rowIndex + ".");
		Session thisSession = OperationsWithDatabase.searchSession(monthNumb, Integer.valueOf(sessionsMonth.getValueAt(rowIndex, 0).toString()), 
				sessionsMonth.getValueAt(rowIndex, 1).toString(), sessionsMonth.getValueAt(rowIndex, 2).toString());
		new SessionProfile(thisSession, this).show();
		MonthlyPlan.setVisible(false);
	}
	
	/**
	 * Метод перехвата исключения отсутствия выбора.
	 * @param choiceMP - выбор пользователя у поля "Список сеансов на месяц".
	 * @param choiceRep - выбор пользователя у поля "Репертуар на месяц".
	 * @throws ChoiceReportException - исключиен отсуствия какого-либо выбора.
	 */
	private void checkChoiceReport(boolean choiceMP, boolean choiceRep) throws ChoiceReportException {
		if (!choiceMP && !choiceRep) throw new ChoiceReportException();
	}
	
	/**
	 * Метод перехвата исключения отсутствия выбора элемента в JComboBox.
	 * @param selected - выбранный элемент в  JComboBox.
	 * @throws MissSelectSearchException исключение.
	 */
	public void checkSearchField(String selected)  throws MissSelectSearchException{
		if ("Столбец".equals(selected)) throw new MissSelectSearchException();
	}
	
	/**
	 * Перехват ошибки повторного добавления элемента, 
	 * а также некорректного заполнения полей при добавлении элемента.
	 * @param strDate - дата добавляемого сеанса.
	 * @param strTime - время  добавляемого сеанса.
	 * @param strTick - количество непроданных билетов на добавляемый сеанс.
	 * @param strNameFilm - название фильма, демонстрируемого на добавляемом сеансе.
	 * @throws RepeatAddException исключение добавления уже существующего в таблице элемента.
	 * @throws WrongDayException исключение некорректного ввода значений.
	 * @throws EmptyFieldException исключение попытки сохранения элемента с незаполненным(-и) полем(-ями).
	 */
	public void checkAdd(String strDate, String strTime, String strNameFilm) 
			throws RepeatAddException, WrongDayException, WrongTimeException, EmptyFieldException {
		
		if (strDate.length() == 0 || strTime.length() == 0)  
			throw new EmptyFieldException();
		
		if (!strDate.matches("([1-9]|1[0-9]|2[0-9]|3[0-1])")) 
			throw new WrongDayException(); 
		
		if (!strTime.matches("^\\d{1,2}:\\d{1,2}$")) {
			throw new WrongTimeException(); 
		}
		System.out.println("@@");
		String[] splitTime = strTime.split(":");
		if (splitTime.length != 2 && !splitTime[0].matches("(0[0-9]|1[0-9]|2[0-3])") 
				&& !splitTime[1].matches("(0[0-9]|1[0-9]|2[0-9]|3[0-9]|4[0-9]|5[0-9])"))
			throw new WrongTimeException(); 
		
		checkRepeat(strNameFilm, strDate, strTime);
	}
	
	public void checkRepeat(String strNameFilm, String strDate, String strTime) throws RepeatAddException {
		int colTime = 1, colFilm = 2, colDate = 0;
		
		for (int i = 0; i < sessionsMonth.getRowCount(); i++) {
			if (modelSessions.getValueAt(i, colFilm).equals(strNameFilm) 
					&& modelSessions.getValueAt(i, colDate).equals(strDate) &&
					modelSessions.getValueAt(i, colTime).equals(strTime)) {
				 throw new RepeatAddException();
			}
		}
	}
	
	/**
	 * Класс исключения ошибки пустого выбора при генерации отчета по спискам ПК.
	 */
	private class ChoiceReportException extends Exception {
		public ChoiceReportException() {
			super("Выберите хотя бы одну таблицу из списка!");
		}
	}
	
	/**
	 * Класс исключения отсутствия выбора элемента в JComboBox.
	 */
	public class MissSelectSearchException extends Exception {
		public MissSelectSearchException() {
			super("Выберите столбец для поиска!");
		}
	}
	
	/**
	 * Класс исключения повторного добавления элемента.
	 */
	public class RepeatAddException extends Exception {
		public RepeatAddException() {
			super("В этот день на это время уже есть сеанс с таким фильмом!");
		}
	}
	
	/**
	 * Класс исключения некорректно введенных данных даты.
	 */
	public class WrongDayException extends Exception {
		public WrongDayException() {
			super("<html>Введенной даты не существует, повторите попытку.</html>");
		}
	}
	
	/**
	 * Класс исключения некорректно введенных данных.
	 */
	public class WrongTimeException extends Exception {
		public WrongTimeException() {
			super("<html>Введенное время либо не может существовать, либо не соответствует формату!<br />Обратите внимание, что время должно быть введено как '**:**'</html>");
		}
	}
	
	/**
	 * Класс исключения пустых полей для ввода.
	 */
	public static class EmptyFieldException extends Exception {
		public EmptyFieldException() {
			super("Заполните все доступные поля!");
		}
	}
}
        
