package sp.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.*;

import org.apache.log4j.Logger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import sp.back.HelperMonth;
import sp.back.Singleton;
import sp.entities.*;
import sp.gui.MonthlyPlan.RepeatAddException;

/**
 * Класс экранной формы сеанса. 
 * @author Арина Морик
 */
public class SessionProfile {
	
	/** Номер месяца открытого сеанса **/
	private int monthNumb;
	/** Экземпляр класса Session с информацией об открытом сеансе **/
	private Session thisSess;
	
	/** День сеанса **/ 
	private String daySess;
	/** Время сеанса **/
	private String timeSess;
	/** Демонстрируемый на сеансе фильм **/
	private Film filmSess;
	
	/** Домашний фрейм, откуда был совершен переход **/
	private final MonthlyPlan homePage;
	
	/** Обводка для названия столбцов таблиц **/
	private static final MatteBorder borderForTable = new MatteBorder(0, 1, 1, 1, java.awt.Color.black);
	/** Обводка для самой таблицы **/
	private static final MatteBorder borderForHead = new MatteBorder(2, 2, 0, 4, java.awt.Color.black);
	/** Шрифт для названия столбцов таблиц **/
	private static final Font fontForHead = new Font("Arial", Font.BOLD, 13);
	/** Шрифт для текста внутри ячеек таблицы **/
	private static final Font fontForTable = new Font("Arial", Font.PLAIN, 12);
	/** Размер прокрутки **/
	private static final Dimension prefSizeScroll = new Dimension(10, 100);
	/** Размер панелей с таблицами **/
	private static final Dimension prefSizePanelTables = new Dimension(300, 400);

	/** Экранная форма **/
	private JFrame sessionFrame;

	/** Кнопка редактирования информации о сеансе **/
	private JButton edit;
	/** Кнопка удаления сеанса **/
	private JButton delete;

	/** Наименование экранной формы **/
	private JLabel pageName;
	/** Кнопка возвращения на предыдущий экран **/
	private JButton goBack;

	/** Панель с информацией о сеансе**/
	private JPanel fieldsInfo;
	/** День сеанса на панели с информацией **/
	private JLabel valueDate;
	/** Время сеанса на панели с информацией **/
	private JLabel valueTime;
	/** Название фильма на панели с информацией **/
	private JLabel valueFilm;
	/** Плашка с именем-фамилией режиссера **/
	private JLabel valueDirector;
	/** Плашка с жанром **/
	private JLabel valueGenre;

	/** Наименование таблицы с забронированными билетами **/
	private JLabel nameTableSold;
	/** Наименование таблицы с незабронированными билетами **/
	private JLabel nameTableFree;
	/** Значение ячеек таблицы с незабронированными билетами **/
	private String[][] ticketFree;
	/** Значение ячеек таблицы с забронированными билетами **/
	private String[][] ticketSold;
	/** Модель таблицы с забронированными билетами **/
	private DefaultTableModel modelTableSoldTick;
	/** Модель таблицы с незабронированными билетами **/
	private DefaultTableModel modelTableFreeTick;
	/** Таблицы с забронированными билетами **/
	private JTable soldTicks;
	/** Таблицы с незабронированными билетами  **/
	private JTable freeTicks;

	/** Панель таблицы с забронированными билетами  **/
	private JPanel panelForSold;
	/** Панель таблицы с незабронированными билетами **/
	private JPanel panelForFree;

	/** Панель с поиском по таблице незабронированных билетов **/
	private JPanel searchPanelFree;
	/** Текстовое поле для ввода значения ряда, билет на место на котором ищется по таблице с незабронированными билетами **/
	private JTextField searchRowFree;
	/** Текстовое поле для ввода значения номера места на ряду, 
	 * билет на место на котором ищется по таблице с незабронированными билетами **/
	private JTextField searchPlaceFree;
	/** Кнопка "Поиска" под таблицей с незабронированными билетами **/
	private JButton filterForFree;
	/** Текстовое поле для ввода значения ряда, билет на место на котором ищется по таблице с забронированными билетами **/
	private JTextField searchRowSold;
	/** Текстовое поле для ввода значения номера места на ряду, 
	 * билет на место на котором ищется по таблице с незабронированными билетами **/
	private JTextField searchPlaceSold;
	/** Кнопка "Поиска" под таблицей с забронированными билетами **/
	private JButton filterForSold;

	/** Сортировщик таблицы с незабронированными билетами для поиска по оной **/
	private TableRowSorter<TableModel> sorterFree;
	/** Сортировщик таблицы с забронированными билетами для поиска по оной **/
	private TableRowSorter<TableModel> sorterSold;
	
	/** Текстовое поле ввода новой даты сеанса при редактировании **/
	private JTextField newDate;
	/** Текстовое поле ввода нового времени сеанса при редактировании **/
	private JTextField newTime;
	/** Выплывающий список доступных для выбора фильмов при редактировании **/
	private JComboBox<String> newFilm;
	
	/** Количество билетов свободных для бронирования **/
	private int countFree;
	/** Количество забронированных билетов **/
	private int countSold;
	
	/** Логгер класса **/
	private static final Logger log = Logger.getLogger(SessionProfile.class.getName());
	
	/**
	 * Конструктор класса экранной формы подробной информации о сеансе
	 * @param sessionFromList - экземпляр Session открытого сеанса
	 * @param planFrame - домашняя страница
	 */
	public SessionProfile(Session sessionFromList, MonthlyPlan planFrame) {
		// Заполнение переменных, непосредственно касающихся сеанса на экранной форме, 
		// получаются из экр. формы расписания сеансов.
		this.thisSess = sessionFromList;
		this.daySess = String.valueOf(thisSess.getDate());
		this.timeSess = thisSess.getTime();
		this.filmSess = thisSess.getFilm();
		this.monthNumb = sessionFromList.getMonth();
		this.homePage = planFrame;
		log.info("Создание экрана с подробной информацией о сеансе " + thisSess.getDate() + 
				" " + HelperMonth.GenitiveCase(monthNumb) + " в " + thisSess.getTime());
		
		// Получение списков билетов из базы данных.
		getTicketsFromDatabase();

		// Создание и форматирование экранной формы.
		sessionFrame = new JFrame("Кинотеатр");
		sessionFrame.setSize(700, 750);
		sessionFrame.setLocationRelativeTo(null);
		sessionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		sessionFrame.setIconImage(new ImageIcon("./img/mediaplayer.png").getImage());
		
		// Создание панели инструментов
		JToolBar toolBar = new JToolBar();
		toolBar.setBounds(100, 100, 300, 300);
		edit = new JButton(new ImageIcon("./img/edit.png"));
		edit.setToolTipText("Изменить информацию о сеансе");
		delete = new JButton(new ImageIcon("./img/delete.png"));
		delete.setToolTipText("Удалить сеанс");
		toolBar.add(edit);
		toolBar.add(delete);
		
		// Создание верхней панели с информацией о вкладке и кнопки, позволяющей вернуться назад.
		JPanel pageInfo = new JPanel();
		pageInfo.setBackground(Color.darkGray);
		pageInfo.setLayout(new BorderLayout());
		pageName = new JLabel("Информация о сеансе " + daySess + " " + HelperMonth.GenitiveCase(monthNumb) + " в " + timeSess);
		pageName.setForeground(Color.white);
		pageName.setFont(new Font("Arial", Font.BOLD, 25));
		pageName.setHorizontalAlignment(JLabel.CENTER);
		
		goBack = new JButton(new ImageIcon("./img/back-new.png"));
		goBack.setToolTipText("Вернуться назад");
		goBack.setPreferredSize(new Dimension(40, 40));

		pageInfo.add(pageName, BorderLayout.CENTER);
		pageInfo.add(goBack, BorderLayout.WEST);

		// Отображение информации о фильме на экране
		fullInfoFields();
		
		// Объединение элементов на экране
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(pageInfo, BorderLayout.NORTH);
		mainPanel.add(toolBar, BorderLayout.CENTER);
		mainPanel.add(fieldsInfo, BorderLayout.SOUTH);
		sessionFrame.add(mainPanel, BorderLayout.NORTH);
		
		// Создание таблиц для билетов
		createTables();
		
		// Добавление слушателей кнопок.
		addActions();
	} 
	
	
	/**
	 * Открытие экранной формы.
	 */
	public void show() {
		log.info("Открытие экранной формы с подробной информацией о сеансе.");
		sessionFrame.setVisible(true);
	}
	
	/**
	 * Метод добавления слушателей кнопкам.
	 */
	private void addActions() {
		
		// Кнопка возвращения на домашнюю страницу.
		goBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				log.info("Возвращение с экрана 'Подробно' на экранную форму с расписанием сеансов.");
				homePage.show();
				sessionFrame.setVisible(false);
			}
		});
		
		// Кнопка удаления сеанса.
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				log.debug("Нажатие кнопки 'удалить'.");
            	int result = JOptionPane.showConfirmDialog(sessionFrame,
                        "<html>Вы уверены, что хотите удалить?<br />Отменить это действие будет невозможно.</html>",
                        "Предупреждение",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        new ImageIcon("./img/warning.png"));
            	if (result == JOptionPane.YES_OPTION) {
            		int[] delItemID = homePage.getSelectedItemID();
            		homePage.getModel().removeRow(delItemID[0]);
            		log.info("Удаление из таблицы сеансов строки с индексом " + delItemID[0]);
            		EntityManager em = Singleton.createEMandTrans();
					Session entity = em.merge(thisSess);
					em.remove(entity);
					Singleton.finishEMandTrans(em);
            		homePage.show();
            		sessionFrame.setVisible(false);
            		JOptionPane.showMessageDialog(homePage.getFrame(),
		                      	"Элемент успешно удален.",
		                      	"Успешное завершение", JOptionPane.INFORMATION_MESSAGE,
		                      	new ImageIcon("./img/check.png"));
            	}
			}
		});
		
		
		// Кнопка редактирования информации о сеансе.
		edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				int result = editElem();
				if (result == JOptionPane.YES_OPTION) {
					daySess = newDate.getText().toString();
					timeSess = newTime.getText().toString();
					Film[] allFilms = (Film[]) homePage.getListOfFilms();
					try {
						checkEdit(daySess, timeSess);
						String titleNewFilm = newFilm.getSelectedItem().toString();
						for (Film movie : allFilms) {
							if (movie.getTitle().equals(titleNewFilm))
								filmSess = movie;
						}
						homePage.checkRepeat(filmSess.getTitle(), daySess, timeSess);
						updateInfo();
						log.info("Успешное изменение информации о сеансе");
						JOptionPane.showMessageDialog(sessionFrame,
		                      	"Информация успешно изменена.",
		                      	"Успешное завершение", JOptionPane.INFORMATION_MESSAGE,
		                      	new ImageIcon("./img/check.png"));
					} catch (WrongEditException ex) {
						log.warn("Попытка изменения информации о сеансе с неверно заполненным полем.", ex);
						JOptionPane.showMessageDialog(sessionFrame, ex.getMessage(),
			                    "Ошибка", JOptionPane.INFORMATION_MESSAGE,
			                    new ImageIcon("./img/warning.png"));
					} catch (EmptyFieldException ex) {
						log.warn("Попытка изменения информации о сеансе с пустым полем.", ex);
						JOptionPane.showMessageDialog(sessionFrame, ex.getMessage(),
			                    "Ошибка", JOptionPane.INFORMATION_MESSAGE,
			                    new ImageIcon("./img/warning.png"));
					} catch (RepeatAddException ex) {
						log.warn("Попытка изменения информации о сеансе так, что в общем списке окажется два одинаковых сеанса.", ex);
						JOptionPane.showMessageDialog(sessionFrame, ex.getMessage(),
			                    "Ошибка", JOptionPane.INFORMATION_MESSAGE,
			                    new ImageIcon("./img/warning.png"));
					}
				}
			}
		});
		
		// Кнопка поиска по таблице незабронированных билетов. 
		filterForFree.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				RowFilter<Object, Object> filter = new RowFilter<Object, Object>() {
					public boolean include(Entry<? extends Object, ? extends Object> entry) {
						for (int i = entry.getValueCount() - 1; i >= 0; i--) {
					    	String valRow = entry.getStringValue(1);
						    String valSeat = entry.getStringValue(2);
						    String userRow = searchRowFree.getText().toString();
						    String userSeat = searchPlaceFree.getText().toString();
					        if ((valRow.equals(userRow) || userRow.length() == 0) 
					        		&& (valSeat.equals(userSeat) || userSeat.length() == 0)) {
					        	return true;
					        	}
					        }
					        return false;
						}
				};
				sorterFree.setRowFilter(filter);
			}
		});

		// Кнопка поиска по таблице забронированных билетов. 
		filterForSold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				RowFilter<Object, Object> filter = new RowFilter<Object, Object>() {
					public boolean include(Entry<? extends Object, ? extends Object> entry) {
						for (int i = entry.getValueCount() - 1; i >= 0; i--) {
							String valRow = entry.getStringValue(0);
							String valSeat = entry.getStringValue(1);
							String userRow = searchRowSold.getText().toString();
							String userSeat = searchPlaceSold.getText().toString();
							if ((valRow.equals(userRow) || userRow.length() == 0) 
									&& (valSeat.equals(userSeat) || userSeat.length() == 0)) {
								return true;
							}
						}
						return false;
					}
				};
				sorterSold.setRowFilter(filter);
			}
		});
	}
	
	/**
	 * Перехват исключения во время редактирования полей даты и фильма.
	 * @param strDate - поле даты 
	 * @param strTime - поле времени
	 * @throws WrongEditException - исключение некорректного значения в поле
	 * @throws EmptyFieldException - исключение пустого значения в поле
	 */
	private void checkEdit(String strDate, String strTime) throws WrongEditException, EmptyFieldException {
		if (strDate.length() == 0 || strTime.length() == 0)  
			throw new EmptyFieldException();
		if (!strDate.matches("([1-9]|1[0-9]|2[0-9]|3[0-1])")) 
			throw new WrongEditException(); 
		String[] splitTime = strTime.split(":");
		if (splitTime.length != 2 || !splitTime[0].matches("(0[0-9]|1[0-9]|2[0-3])") 
				|| !splitTime[1].matches("(0[0-9]|1[0-9]|2[0-9]|3[0-9]|4[0-9]|5[0-9])"))
			throw new WrongEditException(); 
	}
	
	/**
	 * Открытие окна при редактировании информации о сеансе.
	 * @return result - выбор пользователя.
	 */
	private int editElem() {
		int result;
		JPanel oldInfo = new JPanel(new GridLayout(3, 4));
		
		oldInfo.add(new JLabel("День сеанса: ", 10));
		newDate = new JTextField(daySess, 10);
		oldInfo.add(newDate);
		oldInfo.add(new JLabel(" " + HelperMonth.GenitiveCase(monthNumb)));
		
		oldInfo.add(new JLabel("Время показа: ", 10));
		newTime = new JTextField(timeSess, 10);
		oldInfo.add(newTime);
		oldInfo.add(new JLabel(""));
		
		oldInfo.add(new JLabel("Фильм: ", 10));
		List<Film> allFilms = homePage.getArrayOfFilms();
		List<String> movies = allFilms.stream().map(Film::getTitle).collect(Collectors.toList());
		movies = new ArrayList<>(new HashSet<>(movies));
		String[] titlesFilms = (String[]) movies.toArray(new String[movies.size()]);
		Arrays.sort(titlesFilms);
		newFilm = new JComboBox<String>(titlesFilms);
		newFilm.setSelectedItem(filmSess.getTitle());
		oldInfo.add(newFilm);
		oldInfo.add(new JLabel(""));
		
		result = JOptionPane.showOptionDialog(sessionFrame, oldInfo, "Создание нового объекта", 
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, 
				new ImageIcon("./img/information.png"), null, null);
		return result;
	}
	
	/**
	 * Метод заполнения верхней части экранной формы с информацией об открытом сеансе.
	 */
	private void fullInfoFields() {
		fieldsInfo = new JPanel(new GridLayout(7, 3));
		
		// Строка с информацией о дне показа.
		JLabel infoDate = new JLabel("День показа:   ", SwingConstants.RIGHT);
		infoDate.setFont(new Font("Arial", Font.BOLD, 13));
		infoDate.setBorder(BorderFactory.createMatteBorder(1, 4, 1, 1, Color.BLACK));
		valueDate = new JLabel(daySess, SwingConstants.CENTER);
		valueDate.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 4, Color.BLACK));
		valueDate.setFont(new Font("Arial", Font.ITALIC, 13));
		fieldsInfo.add(infoDate);
		fieldsInfo.add(valueDate);
		
		// Строка с информацией о времени показа.
		JLabel infoTime = new JLabel("Время показа:   ", SwingConstants.RIGHT);
		infoTime.setFont(new Font("Arial", Font.BOLD, 13));
		infoTime.setBorder(BorderFactory.createMatteBorder(0, 4, 1, 1, Color.BLACK));
		valueTime = new JLabel(timeSess, SwingConstants.CENTER);
		valueTime.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 4, Color.BLACK));
		valueTime.setFont(new Font("Arial", Font.ITALIC, 13));
		fieldsInfo.add(infoTime);
		fieldsInfo.add(valueTime);
		
		// Строка с информацией о названии демонстрируемого фильма (сделать всплывающее окно о режиссере и прочем?)
		JLabel infoTitleFilm = new JLabel("Демонстрируемый фильм:   ", SwingConstants.RIGHT);
		infoTitleFilm.setFont(new Font("Arial", Font.BOLD, 13));
		infoTitleFilm.setBorder(BorderFactory.createMatteBorder(0, 4, 1, 1, Color.BLACK));
		valueFilm = new JLabel(filmSess.getTitle(), SwingConstants.CENTER);
		valueFilm.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 4, Color.BLACK));
		valueFilm.setFont(new Font("Arial", Font.ITALIC, 13));
		fieldsInfo.add(infoTitleFilm);
		fieldsInfo.add(valueFilm);
		
		JLabel infoDirector =  new JLabel("Режиссер:   ", SwingConstants.RIGHT);
		infoDirector.setFont(new Font("Arial", Font.BOLD, 13));
		infoDirector.setBorder(BorderFactory.createMatteBorder(0, 4, 1, 1, Color.BLACK));
		Info_Film infFilm = filmSess.getInfo();
		valueDirector = new JLabel(infFilm.getDirName() + " " + infFilm.getDirLastName(), SwingConstants.CENTER);
		valueDirector.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 4, Color.BLACK));
		valueDirector.setFont(new Font("Arial", Font.ITALIC, 13));
		fieldsInfo.add(infoDirector);
		fieldsInfo.add(valueDirector);
		
		JLabel infoGenre =  new JLabel("Жанр:   ", SwingConstants.RIGHT);
		infoGenre.setFont(new Font("Arial", Font.BOLD, 13));
		infoGenre.setBorder(BorderFactory.createMatteBorder(0, 4, 2, 1, Color.BLACK));
		valueGenre = new JLabel(infFilm.getGenre(), SwingConstants.CENTER);
		valueGenre.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 4, Color.BLACK));
		valueGenre.setFont(new Font("Arial", Font.ITALIC, 13));
		fieldsInfo.add(infoGenre);
		fieldsInfo.add(valueGenre);
		
	}
	
	/**
	 * Обновление информации о сеансе.
	 */
	private void updateInfo() {
		// Внесение изменений в шапку экрана.
		pageName.setText("Информация о сеансе " + daySess + " " + 
					HelperMonth.GenitiveCase(monthNumb) + " в " + timeSess);
		valueDate.setText(daySess);
		valueTime.setText(timeSess);
		valueFilm.setText(filmSess.getTitle());
		Info_Film infFilm = filmSess.getInfo();
		valueDirector.setText(infFilm.getDirName() + " " + infFilm.getDirLastName());
		valueGenre.setText(infFilm.getGenre());
		
		// Внесение изменений в таблицу сеансов.
		DefaultTableModel homeModel = homePage.getModel();
		int setRow = homePage.getSelectedItemID()[0];
		homeModel.setValueAt(daySess, setRow, 0);
		homeModel.setValueAt(timeSess, setRow, 1);
		homeModel.setValueAt(filmSess.getTitle(), setRow, 2);
		
		// Внесение изменений в БД.
		EntityManager em = Singleton.createEMandTrans();
		thisSess.setDate(Integer.valueOf(daySess));
		thisSess.setFilm(filmSess);
		thisSess.setTime(timeSess);
		em.merge(thisSess);
		Singleton.finishEMandTrans(em);
	}
	
	/**
	 * Получение списка билетов из базы данных. 
	 */
	private void getTicketsFromDatabase() {
		log.info("Получение списка билетов на сеанс из базы данных");
		List<Ticket> allTickets = thisSess.getTckts();
		
		// Разделение полученного списка на билеты купленные и нет.
		ArrayList<String[]> valuesBought = new ArrayList<String[]>(25);
		ArrayList<String[]> valuesFree = new ArrayList<String[]>(25);
		for (Ticket tick : allTickets) {
			if (tick.getInfo() == 1) {
				valuesBought.add(tick.getNumber());
			} else {
				String[] seat = new String[] {"", tick.getNumber()[0], tick.getNumber()[1]};
				valuesFree.add(seat);
			}
		}
		
		ticketSold = valuesBought.toArray(new String[0][]);
		ticketFree = valuesFree.toArray(new String[0][]);
	}
	
	/**
	 * Метод создание таблиц с бронированными и не- билетами. 
	 */
	private void createTables() {
		countFree = ticketFree.length;
		countSold = ticketSold.length;
		// Добавление заголовков таблиц с информацией об общем количестве билетов в каждой. 
		nameTableSold = new JLabel("<html>Куплены<br />Количество мест: " + countSold + "</html>");
		nameTableSold.setFont(new Font("Arial", Font.BOLD, 15));
		nameTableSold.setHorizontalAlignment(JLabel.CENTER);
		nameTableFree = new JLabel("<html>Свободны<br />Количество мест:   " + countFree + "</html>");
		nameTableFree.setFont(new Font("Arial", Font.BOLD, 15));
		nameTableFree.setHorizontalAlignment(JLabel.CENTER);
		
		// Названия столбцов таблиц и их содержание.
		String[] columnNamesSold = new String[]{"Ряд", "Место", ""};
		String[] columnNamesFree = new String[]{"", "Ряд", "Место"};
		
		// Создание моделей таблиц, ограничение редактируемости ячеек.
		modelTableSoldTick = new DefaultTableModel(ticketSold, columnNamesSold) {
			public boolean isCellEditable(int row, int col) {
				return col == 2;
			}
		};	
		modelTableFreeTick = new DefaultTableModel(ticketFree, columnNamesFree) {
			public boolean isCellEditable(int row, int col) {
				return col == 0;
			}
		};
		
		// Создание таблицы, ее заполнение. 
		soldTicks = new JTable(modelTableSoldTick);
		setTable(soldTicks, "./img/forward-mini.png");
		freeTicks = new JTable(modelTableFreeTick);
		setTable(freeTicks, "./img/back-mini.png");
		
		// Добавление областей прокрутки для таблиц.
		JScrollPane scrollSold = new JScrollPane(soldTicks);
		scrollSold.getVerticalScrollBar().setPreferredSize(prefSizeScroll);
		JScrollPane scrollFree = new JScrollPane(freeTicks);
		scrollFree.getVerticalScrollBar().setPreferredSize(prefSizeScroll);
		 
		// Объединение всех элементов, касающихся таблиц, в единые панели. 
		panelForSold = new JPanel(new BorderLayout());
        panelForSold.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 50));
        panelForSold.add(nameTableSold,BorderLayout.NORTH);
		panelForSold.add(scrollSold, BorderLayout.CENTER);
		panelForSold.setPreferredSize(prefSizePanelTables);
		
        panelForFree = new JPanel(new BorderLayout());
        panelForFree.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 100));
		panelForFree.add(nameTableFree,BorderLayout.NORTH);
		panelForFree.add(scrollFree, BorderLayout.CENTER);
		panelForFree.setPreferredSize(prefSizePanelTables);
		
		// Добавление поиска по таблицам.
		createSearchField();
		
		// Добавление таблиц на экранную форму.
		sessionFrame.add(panelForFree, BorderLayout.EAST);
		sessionFrame.add(panelForSold, BorderLayout.WEST);
	}
	
	/**
	 * Добавление поля
	 */
	private void createSearchField() {
		// Панель с блоком из текстовых полей для ввода ряда и места искомого билета для таблицы незабронированных билетов.
		searchPanelFree = new JPanel(new GridLayout(3, 3));
		JLabel labelRowFree = new JLabel("Ряд:   ", SwingConstants.RIGHT);
		labelRowFree.setFont(new Font("Arial", Font.BOLD, 13));
		searchRowFree = new JTextField();
		searchPanelFree.add(labelRowFree);
		searchPanelFree.add(searchRowFree);
		
		JLabel labelPlaceFree = new JLabel("Место:   ", SwingConstants.RIGHT);
		labelPlaceFree.setFont(new Font("Arial", Font.BOLD, 13));
		searchPlaceFree = new JTextField();
		searchPanelFree.add(labelPlaceFree);
		searchPanelFree.add(searchPlaceFree);
		
		// Кнопка "Поиск" для таблицы незабронированных билетов.
		filterForFree = new JButton("Поиск");
		searchPanelFree.add(new JLabel(""));
		searchPanelFree.add(filterForFree);
		
		// Панель с блоком из текстовых полей для ввода ряда и места искомого билета 
		// для таблицы забронированных билетов. 
		JPanel searchPanelSold = new JPanel(new GridLayout(3, 3));
		searchRowSold = new JTextField();
		JLabel labelRowSold = new JLabel("Ряд:   ", SwingConstants.RIGHT);
		labelRowSold.setFont(new Font("Arial", Font.BOLD, 13));
		searchPanelSold.add(labelRowSold);
		searchPanelSold.add(searchRowSold);
		
		searchPlaceSold = new JTextField();
		JLabel labelPlaceSold = new JLabel("Место:   ", SwingConstants.RIGHT);
		labelPlaceSold.setFont(new Font("Arial", Font.BOLD, 13));
		searchPanelSold.add(labelPlaceSold);
		searchPanelSold.add(searchPlaceSold);
		// Кнопка "Поиск" для таблицы забронированных билетов.
		filterForSold = new JButton("Поиск");
		searchPanelSold.add(new JLabel(""));
		searchPanelSold.add(filterForSold);
		
		// Сортировщики таблиц.
		sorterFree = new TableRowSorter<TableModel>(modelTableFreeTick);
		freeTicks.setRowSorter(sorterFree);
		sorterSold = new TableRowSorter<TableModel>(modelTableSoldTick);
		soldTicks.setRowSorter(sorterSold);
		
		panelForFree.add(searchPanelFree,  BorderLayout.SOUTH);
		panelForSold.add(searchPanelSold,  BorderLayout.SOUTH);
	}
	
	/**
	 * Метод изменение ширин столбцов таблицы.
	 * @param setTable - таблица, ширина столбцов которой корректируется.
	 */
	
	private void setWidthTable(JTable setTable) {
		
		// номер столбца с кнопкой, чья ширина должна быть больше.
		int numbBiggerColumn = setTable.getColumn("").getModelIndex();
		TableColumn column = null;

		for (int i = 0; i < 3; i++) {
		    column = setTable.getColumnModel().getColumn(i);
		    if (i == numbBiggerColumn) {
		        column.setMaxWidth(100);
		    } else {
		        column.setMaxWidth(80);
		    }
		}
	}
	
	/**
	 * Метод форматирования таблицы.
	 * @param setTable - форматируемая таблицы.
	 * @param fileName - достаточный путь к файлу с изображением кнопки в таблице.
	 */
	
	private void setTable(JTable setTable, String fileName) {
		setTable.getTableHeader().setBorder(borderForHead);
		setTable.setBorder(borderForTable);
		setTable.getTableHeader().setFont(fontForHead);
		setTable.setFont(fontForTable);
		setWidthTable(setTable);
		setTable.getColumn("").setCellRenderer(new ButtonRenderer(fileName));
		setTable.getColumn("").setCellEditor(new ButtonEditor(new JCheckBox(), fileName));
	}
	
	
	/**
	 * Класс визуализации кнопки в таблице.
	 */
	class ButtonRenderer extends JButton implements TableCellRenderer {
		/** Достаточный путь к файлу с изображением для кнопки. **/
		private String fileName; 
		/**
		 * Конструктор класса.
		 * @param fileName - достаточный путь к файлу с изображением для кнопки.
		 */
		public ButtonRenderer(String fileName) { 
			this.fileName = fileName;
		}
		/**
		 * Вид при нажатии.
		 */
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			setIcon(new ImageIcon(fileName));
			return this;
		}
	}
	
	/**
	 * Класс реакции для ячейки таблицы с кнопкой.
	 */
	class ButtonEditor extends DefaultCellEditor {
		/** Достаточный путь к файлу с изображением для кнопки. **/
		private String fileName;
		
		/**
		 * Конструктор класса.
		 * @param checkBox
		 * @param fileName - достаточный путь к файлу с изображением для кнопки.
		 */
		public ButtonEditor(JCheckBox checkBox, String fileName) {
			super(checkBox);
			this.fileName = fileName;
		}
		/**
		 * Реакция на нажатие.
		 */
		public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
				JButton buttonMoving = new JButton(new ImageIcon(fileName));
				// Слушатель кнопки.
				buttonMoving.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						int numbColumnRow = table.getColumn("Ряд").getModelIndex();
						int numbColumnPlace = table.getColumn("Место").getModelIndex();
						String[] place = new String[] {table.getValueAt(row, numbColumnRow).toString(), 
								table.getValueAt(row, numbColumnPlace).toString()};
						Object[] options = {"Да", "Отмена"};
						// Бронирование билета
						if (table == freeTicks) {
							int mode = JOptionPane.showOptionDialog(sessionFrame,
								    "Поставить бронь на билет: ряд " + place[0] + ", место " + place[1] + "?",
								    "Бронь билета",
								    JOptionPane.OK_CANCEL_OPTION,
								    JOptionPane.QUESTION_MESSAGE,
								    new ImageIcon("./img/question.png"),
								    options,
								    null);
							if (mode == JOptionPane.YES_OPTION) {
								thisSess.bookTick(Integer.valueOf(place[0]), Integer.valueOf(place[1]));
								((DefaultTableModel)soldTicks.getModel()).addRow(new Object[]{place[0], place[1]});
								((DefaultTableModel)freeTicks.getModel()).removeRow(row);
								nameTableSold.setText("<html>Куплены<br />Количество мест: " + ++countSold + "</html>");
								nameTableFree.setText("<html>Свободны<br />Количество мест:   " + --countFree + "</html>");
								
							}
						// Отмена брони билета.
						} else {
							int mode = JOptionPane.showOptionDialog(sessionFrame,
								    "Отменить бронь на билет: ряд " + place[0] + ", место " + place[1] + "?",
								    "Бронь билета",
								    JOptionPane.OK_CANCEL_OPTION,
								    JOptionPane.QUESTION_MESSAGE,
								    new ImageIcon("./img/question.png"),
								    options,
								    null);
							if (mode == JOptionPane.YES_OPTION) {
								thisSess.freeTick(Integer.valueOf(place[0]), Integer.valueOf(place[1]));
								((DefaultTableModel)freeTicks.getModel()).addRow(new Object[]{"", place[0], place[1]});
								((DefaultTableModel)soldTicks.getModel()).removeRow(row);
								countSold--;
								countFree++;
								nameTableSold.setText("<html>Куплены<br />Количество мест: " + countSold + "</html>");
								nameTableFree.setText("<html>Свободны<br />Количество мест:   " + countFree + "</html>");
							}
						}
					}
				});
				return buttonMoving;
			}
	}
	
	/**
	 * Класс исключения некорректно введенных данных.
	 */
	public class WrongEditException extends Exception {
		public WrongEditException() {
			super("Некорректный формат введенной строки!");
		}
	}
	
	/**
	 * Класс исключения пустых полей для ввода.
	 */
	public class EmptyFieldException extends Exception {
		public EmptyFieldException() {
			super("Заполните все доступные поля!");
		}
	}
}
