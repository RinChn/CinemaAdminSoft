package sp.gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import com.itextpdf.text.DocumentException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import sp.back.*;
import sp.entities.Film;
import sp.entities.Info_Film;

import org.apache.log4j.Logger;


/**
 * Экранная форма репертуара.
 * @author Арина Морик
 * @version 1.0
 *
 */
public class Repertoire {
	
	/** Кнопка возвращения на главный экран **/
	public JButton goBack;
	
	/** Контейнер, содержащий в себе все элементы экранной формы **/
	private JFrame repertoireWindow;

	/** Кнопка сохранения списка в файл **/
	private JButton save;
	/** Кнопка загрузки списка из файла **/
	private JButton load;
	/** Кнопка добавления нового элемента в список **/
	private JButton addNewElem;
	/** Кнопка генерации отчёта **/
	private JButton createReport;
	
	/** Модель таблицы фильмов **/
	public DefaultTableModel modelFilms;
	/** Таблица информации о фильмах **/
	private JTable films;
	/** Массив названий столбцов таблицы **/
	private String[] columnNames;
	/** Массив значений строк таблицы **/
	private String[][] dataValues;
	/** Фильтрация списка по заданным значениям **/
	private TableRowSorter<TableModel> sorter;
	
	/** Выпадающий список доступных столбцов для поиска **/
	private JComboBox<String> listOfColumns;
	/** Кнопка поиска **/
	private JButton filter;
	/** Поле ввода поискового запроса по таблице**/
	private JTextField searchQuery;

	/** Введенное пользователем наименование фильма для добавления в таблицу **/
	private JTextField newMovieName;
	/** Выпадающий список режиссеров **/
	private JComboBox<String> newDir;
	/** Выпадающий список жанров фильмов **/
	private JComboBox<String> newGenre;
	/** Введенная пользователем длительность фильма для добавления в таблицу **/
	private JTextField newLength;
	/** Введенная пользователем дата премьеры фильма для добавления в таблицу **/
	private JTextField newPremiere;

	/** Кнопка удаления элемента списка в popupMenu **/
	public JMenuItem deleteRowMovie;
	/**  Кнопка редактирования элемента списка в popupMenu **/
	private JMenuItem editItem;
	
	/** Список доступных для выбора режиссеров **/
	private String[] allDirectors;
	/** Список доступных для выбора жанров **/
	private String[] allGenres;
	
	/** Страница месячного плана, с которой был совершен переход **/
	private MonthlyPlan home;
	
	/** Логгер класса **/
	private static final Logger log = Logger.getLogger(Repertoire.class.getName());

	/**
	 * Конструктор класса.
	 */
	public Repertoire(MonthlyPlan homePage) {
		
		this.home = homePage;
		
		// создание и форматирование экранной формы.
		repertoireWindow = new JFrame("Кинотеатр"); 
		repertoireWindow.setSize(700, 750);
		repertoireWindow.setLocationRelativeTo(null);
		repertoireWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		repertoireWindow.setIconImage(new ImageIcon("./img/mediaplayer.png").getImage());
		
		// создание верхней панели с информацией о вкладке и кнопке, позволяющей вернуться назад.
		JPanel pageInfo = new JPanel(new BorderLayout());
		pageInfo.setBackground(java.awt.Color.darkGray);
		JLabel pageName = new JLabel("Репертуар");
		pageName.setForeground(Color.white);
		pageName.setFont(new Font("Arial", Font.BOLD, 25));
		pageName.setHorizontalAlignment(JLabel.CENTER);
		goBack = new JButton(new ImageIcon("./img/back-new.png"));
		goBack.setForeground(Color.black);
		goBack.setPreferredSize(new Dimension(40, 40));
		pageInfo.add(pageName, BorderLayout.CENTER);
		pageInfo.add(goBack, BorderLayout.WEST);
		
		// создание панели инструментов.
		JToolBar toolBar = new JToolBar();
		toolBar.setBounds(100, 100, 300, 300);
		save = new JButton(new ImageIcon("./img/save.png"));
		load = new JButton(new ImageIcon("./img/download.png"));;
		addNewElem = new JButton(new ImageIcon("./img/add.png"));
		createReport = new JButton(new ImageIcon("./img/pdf.png"));
		save.setToolTipText("Сохранить в файл");
		load.setToolTipText("Загрузить новые данные");
		addNewElem.setToolTipText("Добавить фильм в репертуар");
		createReport.setToolTipText("Сохранить отчёт");
		toolBar.add(save);
		toolBar.add(load);
		toolBar.add(createReport);
		toolBar.add(addNewElem);
		
		// объединение двух панелей в одну, добавление на экранную форму.
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(pageInfo, BorderLayout.NORTH);
		mainPanel.add(toolBar, BorderLayout.SOUTH);
		repertoireWindow.setLayout(new BorderLayout());
		repertoireWindow.add(mainPanel, BorderLayout.NORTH);
		
		// создание таблицы с данными о фильмах, прикрепление  ее к фрейму.
		columnNames = new String[]{"Название фильма", "Режиссер", "Жанр", "<html>Длительность<br />(мин)</html>", "Дата премьеры"};
		getMoviesFromDatabase();
		modelFilms = new DefaultTableModel(dataValues, columnNames) {
			public boolean isCellEditable(int i, int i1) {
				return false;
			}
		};
		films = new JTable(modelFilms);
		films.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
		JScrollPane scroll = new JScrollPane(films);
		films.setFont(new Font("Arial", Font.PLAIN, 12));
		films.getTableHeader().setPreferredSize(new Dimension(100, 30));
		films.getTableHeader().setReorderingAllowed(false);
		repertoireWindow.add(scroll, BorderLayout.CENTER);
		
		// создание списка столбцов таблицы для поиска в ней и кнопки поиска.
		listOfColumns = new JComboBox<String>(new String[]
				{"Столбец", "Название фильма", "Режиссер", "Жанр"});
		searchQuery = new JTextField("");
		searchQuery.setColumns(30);
		filter = new JButton("Поиск");
		filter.setFont(new Font("Arial", Font.BOLD, 13));
		sorter = new TableRowSorter<TableModel>(modelFilms);
		films.setRowSorter(sorter);
		
		// создание панели поиска, прикрепление ее к экр. форме.
		JPanel filterPanel = new JPanel();
		filterPanel.setBackground(Color.gray);
		filterPanel.add(listOfColumns);
		filterPanel.add(searchQuery);
		filterPanel.add(filter);
		repertoireWindow.add(filterPanel, BorderLayout.SOUTH);
		
		// создание меню при клике пкм по элементу таблицы, добавление кнопки удаления выделенного элемента.
		JPopupMenu popupMenu = new JPopupMenu();
        deleteRowMovie = new JMenuItem("Удалить");
        editItem = new JMenuItem("Редактировать");
        popupMenu.add(deleteRowMovie);
        popupMenu.add(editItem);
        films.setComponentPopupMenu(popupMenu);

		// добавление слушателей кнопок.
		addActions();

	}
	
	/**
	 * Метод демонстрации фрейма.
	 */
	public void show() {
		log.info("Открытие экранной формы");
		repertoireWindow.setSize(700, 750);
		repertoireWindow.setLocationRelativeTo(null);
		repertoireWindow.setVisible(true);
	}
	
	/**
	 * Геттер модели таблицы.
	 * @return modelFilms - модель таблицы.
	 */
	public DefaultTableModel getModel() {
		return modelFilms;
	}
	
	/**
	 * Геттер самой таблицы.
	 * @return films - таблица фильмов.
	 */
	public JTable getTable() {
		return films;
	}
	
	/**
	 * Метод скрытия фрейма.
	 */
	public void close() {
		log.info("Закрытие экранной формы");
		repertoireWindow.setVisible(false);
	}
	
	/**
	 * Метод получения списка данных о фильмов, списков жанров и режиссеров.
	 */
	private void getMoviesFromDatabase() {
		log.info("Получение списка фильмов на месяц " + home.getMonthNumb() + " из базы данных");
		Film[] films = (Film[]) home.getListOfFilms();
		dataValues = new String[films.length][5];
		for (int i = 0; i < films.length; i++) {
			Film elemNow = films[i];
			Info_Film infoElem = elemNow.getInfo();
			dataValues[i][0] = elemNow.getTitle();
			dataValues[i][1] = infoElem.getDirName() + " " + infoElem.getDirLastName();
			dataValues[i][2] = infoElem.getGenre();
			dataValues[i][3] = String.valueOf(elemNow.getLength());
			dataValues[i][4] = elemNow.getPremiere();
		}
		
		allDirectors = OperationsWithDatabase.getSortListDirectors();
		allGenres = OperationsWithDatabase.getSortListGenres();
	}
	
	/**
	 * Метод удаления всех сеансов с удаляемым фильмом.
	 * @param delFilm - название удаляемого фильма.
	 */
	private void deleteSessionsWithMovieTable(String delFilm) {
		DefaultTableModel sessions = home.getModel();
		for (int i = 0; i < sessions.getRowCount();) {
			if (sessions.getValueAt(i, 2).equals(delFilm))
				sessions.removeRow(i);
			else 
				i++;
		}
		
		Film[] films = (Film[]) home.getListOfFilms();
		for (int i = 0; i < films.length; i++) 
			if (films[i].getTitle().equals(delFilm)) 
				home.deleteFilmList(films[i]);
	}
	
	/**
	 * Метод добавления слушателей кнопкам.
	 */
	private void addActions() {
        UIManager.put("OptionPane.cancelButtonText", "Отмена");
        UIManager.put("OptionPane.okButtonText", "Ок");
		UIManager.put("OptionPane.yesButtonText", "Да");
		UIManager.put("OptionPane.noButtonText", "Отмена");
		
		// Кнопка возврата на главную страницу.
		goBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	close();
            	home.show();
            }
		});
		
		// Кнопка удаления элемента таблицы.
        deleteRowMovie.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	log.debug("Нажатие кнопки 'удалить'.");
            	int result = JOptionPane.showConfirmDialog(repertoireWindow,
                        "<html>Вы точно хотите удалить?<br />Учтите, что все сеансы с данным фильмом также будут удалены.</html>",
                        "Предупреждение",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        new ImageIcon("./img/warning.png"));
            	if (result == JOptionPane.YES_OPTION) {
            		int rowIndex = films.convertRowIndexToModel(films.getSelectedRow());
            		String titleDeleteFilm = modelFilms.getValueAt(rowIndex, 0).toString();
            		String lengthDeleteFilm = modelFilms.getValueAt(rowIndex, 3).toString();
            		String dirDeleteFilm = modelFilms.getValueAt(rowIndex, 1).toString();
            		String genreDeleteFilm = modelFilms.getValueAt(rowIndex, 2).toString();
            		Info_Film info = OperationsWithDatabase.convertInfo(dirDeleteFilm, genreDeleteFilm);
            		String premDeleteFilm = modelFilms.getValueAt(rowIndex, 4).toString();
            		deleteSessionsWithMovieTable(titleDeleteFilm); 
            		log.info("Удаление строки с индексом " + rowIndex);
            		modelFilms.removeRow(rowIndex);
            		EntityManager em = Singleton.createEMandTrans();
            		Query query = em.createQuery("DELETE FROM Session e WHERE e.numb_month = :numb_month AND e.film =: film");
            		query.setParameter("numb_month", home.getMonthNumb());
            		query.setParameter("film", searchFilm(em, titleDeleteFilm, lengthDeleteFilm, premDeleteFilm, info));
            		query.executeUpdate();
            		Singleton.finishEMandTrans(em);
            		JOptionPane.showMessageDialog(null,
                            "<html>Фильм '" + titleDeleteFilm + "' успешно удален из репертуара.<br />"
                            		+ "Все сеансы с этим фильмом также удалены.</html>",
                            "Успешное завершение операции", JOptionPane.INFORMATION_MESSAGE,
                            new ImageIcon("./img/check.png"));
            	}
            }
        });
        
        // Кнопка изменения ячейки таблицы.
        editItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	log.debug("Нажатие кнопки 'редактировать'.");
            	int columnIndex = films.getSelectedColumn(); 
            	int rowIndex = films.convertRowIndexToModel(films.getSelectedRow()); 
            	// Получение нынешних данных элемента
            	String titleEditFilm = modelFilms.getValueAt(rowIndex, 0).toString();
        		String lengthEditFilm = modelFilms.getValueAt(rowIndex, 3).toString();
        		String dirEditFilm = modelFilms.getValueAt(rowIndex, 1).toString();
        		String genreEditFilm = modelFilms.getValueAt(rowIndex, 2).toString();
        		Info_Film info = OperationsWithDatabase.convertInfo(dirEditFilm, genreEditFilm);
        		String premEditFilm = modelFilms.getValueAt(rowIndex, 4).toString();
        		
            	switch (columnIndex) {
            		// Изменение названия фильма
            		case (0):
            			JOptionPane.showMessageDialog(null,
			                    "<html>Название фильма недоступно для изменения.<br />Пожалуйста, удалите элемент или создайте новый, если это необходимо.</html>",
			                    "Ошибка", JOptionPane.INFORMATION_MESSAGE,
			                    new ImageIcon("./img/warning.png"));
	            		break;
	            	// Изменение имени режиссера
            		case (1):
            			JComboBox<String> choice = new JComboBox<String>(allDirectors);
            			choice.setSelectedItem(films.getValueAt(rowIndex, columnIndex));
	            		int result = JOptionPane.showOptionDialog(repertoireWindow, choice, "Выберите режиссера фильма", 
	            				JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, 
	            				new ImageIcon("./img/choice.png"), null, null);
	            		
	            		if (result == JOptionPane.OK_OPTION) {
	            			EntityManager em = Singleton.createEMandTrans();
	                		Film edFilm = searchFilm(em, titleEditFilm, lengthEditFilm, premEditFilm, info);
	                		edFilm.setInfo(OperationsWithDatabase.convertInfo(choice.getSelectedItem().toString(), edFilm.getInfo().getGenre()));
	                		em.merge(edFilm);
	                		Singleton.finishEMandTrans(em);
	            			films.setValueAt(choice.getSelectedItem().toString(), rowIndex, columnIndex);
	            		}
	            		break;
	            	// Изменение жанра
            		case (2):
            			choice = new JComboBox<String>(allGenres);
            			choice.setSelectedItem(films.getValueAt(rowIndex, columnIndex));
	            		result = JOptionPane.showOptionDialog(repertoireWindow, choice, "Выберите жанр фильма", 
	            				JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, 
	            				new ImageIcon("./img/choice.png"), null, null);
	            		if (result == JOptionPane.OK_OPTION) {
	            			EntityManager em = Singleton.createEMandTrans();
	                		Film edFilm = searchFilm(em, titleEditFilm, lengthEditFilm, premEditFilm, info);
	                		edFilm.setInfo(OperationsWithDatabase.convertInfo(edFilm.getInfo().getDirName() + " " + edFilm.getInfo().getDirLastName(), 
	                				choice.getSelectedItem().toString()));
	                		em.merge(edFilm);
	                		Singleton.finishEMandTrans(em);
	            			films.setValueAt(choice.getSelectedItem().toString(), rowIndex, columnIndex);
	            		}
	            		break;
	            	// Изменение длительности и премьеры
	            	default:
	            		String nameColumn = null;
	            		if (columnIndex == 3) nameColumn = "Длительность (мин)";
		            	else nameColumn = columnNames[columnIndex];
		            	
		            	Object fieldMean = JOptionPane.showInputDialog(repertoireWindow, "Введите новое значение поля '" + nameColumn + "':", 
			            		"Ввод нового значения", JOptionPane.INFORMATION_MESSAGE, 
			            		new ImageIcon("./img/information.png"), null, null);
		            	
		            	try {
			            	checkEdit(columnIndex, fieldMean.toString());
			            	log.info("Редактирование ячейки с индексом (" + columnIndex + ";" + rowIndex + ").");
			            	EntityManager em = Singleton.createEMandTrans();
		                	Film edFilm = searchFilm(em, titleEditFilm, lengthEditFilm, premEditFilm, info);
		                	switch (columnIndex) {
		                	case (3):
		                		edFilm.setLength(Integer.valueOf(fieldMean.toString()));
		                		em.merge(edFilm);
		                		break;
		                	case (4):
		                		edFilm.setPremiere(fieldMean.toString());
		                		em.merge(edFilm);
		                		break;
		                	}
		                	Singleton.finishEMandTrans(em);
			            	films.setValueAt(fieldMean, rowIndex, columnIndex);
			            } catch (EditDigException ex) {
			            	log.warn("Попытка сохранения некорректного значения при редактировании.", ex);
			            	JOptionPane.showMessageDialog(repertoireWindow,
			                           ex.getMessage(), "Ошибка", 
			                           JOptionPane.INFORMATION_MESSAGE, new ImageIcon("./img/warning.png"));
			            } catch (EmptyFieldException ex) {
			            	log.warn("Попытка сохранения пустого поля при редактировании.", ex);
							JOptionPane.showMessageDialog(repertoireWindow,
									ex.getMessage(),
				                    "Ошибка", JOptionPane.INFORMATION_MESSAGE,
				                    new ImageIcon("./img/warning.png"));
						}	
            	}
           
            }
        });
        
        // Кнопка поиска.
		filter.addActionListener(new ActionListener() {
	          public void actionPerformed(ActionEvent e) {
	        	  log.info("Нажатие кнопки 'поиск'.");
		          try {
		        	  String text = searchQuery.getText();
		        	  Object selected = listOfColumns.getSelectedItem();
		        	  sorter.setRowFilter(null);
			          checksearchQueryField(selected);
				      int col = -1; 
				      for (int i = 0; i < 3 && col == -1; i++) {
					      if (columnNames[i].equals(selected)) col = i;
					      }
				      try {
				    	  log.info("Поиск по таблице по запросу '" + selected.toString() + "'.");
				    	  sorter.setRowFilter(RowFilter.regexFilter(text, col));
				      } catch (PatternSyntaxException pse) {
				    	  log.warn("Некорректный паттерн при попытке поиска.", pse);
				          System.err.println("Bad regex pattern");
				      }
		          } catch(SearchQueryException ex) {
		        	  log.warn("При нажатии на кнопку 'поиск' не выбран столбец", ex);
		        	  JOptionPane.showMessageDialog(repertoireWindow,
		                      ex.getMessage(),
		                      "Ошибка", JOptionPane.INFORMATION_MESSAGE,
		                      new ImageIcon("./img/warning.png"));
		          }
	          }
	    });
        
		// Кнопка добавления нового элемента в таблицу.
		addNewElem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				log.debug("Нажатие кнопки 'Создать'.");
				Object[] options = {"Уже существующий фильм",
	                    "Новый фильм",
	                    "Отмена"};
				int mode = JOptionPane.showOptionDialog(repertoireWindow,
				    "Выберите способ добавления фильма в репертуар.",
				    "Добавление элемента",
				    JOptionPane.YES_NO_CANCEL_OPTION,
				    JOptionPane.QUESTION_MESSAGE,
				    new ImageIcon("./img/choice.png"),
				    options,
				    null);
				if (mode == JOptionPane.NO_OPTION) {
					int result = addNewElem();
					if (result == JOptionPane.OK_OPTION) {
						try {
							String strName = newMovieName.getText();
							String strLength = newLength.getText();
							String strPremiere = newPremiere.getText();
							String choiceDir = newDir.getSelectedItem().toString();
							String choiceGenre = newGenre.getSelectedItem().toString();
							checkAdd(strName, strLength, strPremiere, choiceDir);
							log.info("Добавление нового элемента с именем " + strName + " в таблицу");
							((DefaultTableModel)films.getModel()).addRow(new String[]{strName, choiceDir, 
									choiceGenre, strLength,strPremiere});
							Info_Film convertDirAndGenre = OperationsWithDatabase.convertInfo(choiceDir, choiceGenre);
							Film newMovie = OperationsWithDatabase.addMovieInDatabase(strName, strLength, strPremiere, convertDirAndGenre);
							home.addNewFilmList(newMovie);
							JOptionPane.showMessageDialog(repertoireWindow, "Фильм успешно добавлен!",
				                    "Успешно", JOptionPane.INFORMATION_MESSAGE,
				                    new ImageIcon("./img/check.png"));
						} catch (RepeatAddException ex) {
							log.warn("Попытка добавления уже существующего элемента в таблицу.", ex);
							JOptionPane.showMessageDialog(repertoireWindow, ex.getMessage(),
			                    "Ошибка", JOptionPane.INFORMATION_MESSAGE,
			                    new ImageIcon("./img/warning.png"));
						} catch (EditDigException ex) {
							log.warn("Попытка добавления элемента с некорректно заполненными полями.", ex);
							JOptionPane.showMessageDialog(repertoireWindow, ex.getMessage(),
				                    "Ошибка", JOptionPane.INFORMATION_MESSAGE,
				                    new ImageIcon("./img/warning.png"));
						} catch (EmptyFieldException ex) {
							log.warn("Попытка добавления элемента с пустым значением поля.", ex);
							JOptionPane.showMessageDialog(repertoireWindow, ex.getMessage(),
			                    "Ошибка", JOptionPane.INFORMATION_MESSAGE,
			                    new ImageIcon("./img/warning.png"));
							
						} catch (WrongDataException ex) {
							log.warn("Попытка добавления элемента с неверно заполненными полями", ex);
							JOptionPane.showMessageDialog(repertoireWindow, ex.getMessage(),
			                    "Ошибка", JOptionPane.INFORMATION_MESSAGE,
			                    new ImageIcon("./img/warning.png"));
							
						}
					}
				} else if (mode == JOptionPane.YES_OPTION) {
					showTableWithAllFilms();
				}
			}
		});
		
		// Кнопка сохранения списка в файл.
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				log.debug("Нажатие кнопки 'сохранить'.");
				String[][] dataArray = new String[modelFilms.getRowCount()][modelFilms.getColumnCount()];
				for (int i = 0; i < modelFilms.getRowCount(); i++) {
					for (int j = 0; j < modelFilms.getColumnCount();j++) {
						dataArray[i][j] = (String) modelFilms.getValueAt(i, j);
					}
				}
				new FileSave("Выбор файла для сохранения", dataArray, 
					new String[] {"Name", "Director", "Genre", "Length", "Premiere"}, "filmList", "film");
			}
		});
		
		// Кнопка загрузки списка из файла.
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				log.debug("Нажатие кнопки 'загрузить'.");
				Object[] options = {"Дополнить",
	                    "Обновить",
	                    "Отмена"};
				int mode = JOptionPane.showOptionDialog(repertoireWindow,
				    "Выберите режим загрузки данных в список",
				    "Загрузка данных",
				    JOptionPane.YES_NO_CANCEL_OPTION,
				    JOptionPane.QUESTION_MESSAGE,
				    new ImageIcon("./img/choice.png"),
				    options,
				    null);
				if (mode != JOptionPane.CANCEL_OPTION && mode != JOptionPane.CLOSED_OPTION) {
					int result = -1;
					if (mode == JOptionPane.NO_OPTION)
						result = JOptionPane.showConfirmDialog(null,
		                        "<html>Вы точно хотите обновить список?<br />Все сеансы этого месяца будут удалены в случае полного обновления репертуара.</html>",
		                        "Предупреждение",
		                        JOptionPane.YES_NO_OPTION,
		                        JOptionPane.WARNING_MESSAGE,
		                        new ImageIcon("./img/warning.png"));
					if (result == -1 || result == JOptionPane.YES_OPTION) {
						if (result == JOptionPane.YES_OPTION) {
							DefaultTableModel model = (DefaultTableModel) home.getModel();
							while (model.getRowCount() > 0) {
							    model.removeRow(0);
							}
						}
						new FileRead("Выбор файла для загрузки", modelFilms, new int[] {mode, home.getMonthNumb()}, 
							new int[] {0, 1}, new String[] {"Name", "Director", "Genre", "Length", "Premiere"}, "film");
						EntityManager em = Singleton.createEMandTrans();
						for (int i = 0; i < modelFilms.getRowCount(); i++) {
							Film new_Film = searchFilm(em, modelFilms.getValueAt(i, 0).toString(), modelFilms.getValueAt(i, 3).toString(), 
									modelFilms.getValueAt(i, 4).toString(), 
									OperationsWithDatabase.convertInfo(modelFilms.getValueAt(i, 1).toString(), modelFilms.getValueAt(i, 2).toString()));
							home.addNewFilmList(new_Film);
						}
						Singleton.finishEMandTrans(em);
					}
				}
			}
		});
		
		// Кнопка генерации отчета по таблице.
		createReport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				log.debug("Нажатие кнопки 'Сгенерировать отчёт'.");
				try {
					new CreatePDF(modelFilms, "Repertoire for " + HelperMonth.getEnglishName(home.getMonthNumb()), 
							new String[]{"Название фильма", "Режиссер", "Жанр", "Длительность (мин)", "Дата премьеры"},
							new float[]{38f, 38f, 25f, 30f, 22f}, true);
				} catch (DocumentException ex) {
					log.error("Ошибка сохранения в файл.", ex);
					JOptionPane.showMessageDialog(repertoireWindow, ex.getMessage(),
		                    "Ошибка", JOptionPane.INFORMATION_MESSAGE,
		                    new ImageIcon("./img/warning.png"));
				}
			}
		});
		
	}
	
	/**
	 * Метод демонстрации всех фильмов из таблицы в БД.
	 */
	private void showTableWithAllFilms() {
		
		// Получение списка всех фильмов.
		EntityManager em = Singleton.createEMandTrans();
		List<Film> everyFilm = em.createQuery("SELECT e FROM Film e").getResultList();
		
		// Преобразование списка под табличные данные
		String[][] data = new String[everyFilm.size()][5];
		for (int i = 0; i < everyFilm.size(); i++) {
			Film elem = everyFilm.get(i);
			Info_Film infoElem = elem.getInfo();
			data[i][0] = elem.getTitle();
			data[i][1] = infoElem.getDirName() + " " + infoElem.getDirLastName();
			data[i][2] = infoElem.getGenre();
			data[i][3] = String.valueOf(elem.getLength());
			data[i][4] = elem.getPremiere();
		}
		
		// Создание таблицы
        Object[] columnNames = {"Название фильма", "Режиссер", "Жанр", "Длительность", "Дата премьеры"};
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
        	public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(false);
        
        // Создание шапки окна
        JPanel windowInfo = new JPanel();
        windowInfo.setBackground(java.awt.Color.darkGray);
        windowInfo.setLayout(new BorderLayout());
		JLabel infoForUser = new JLabel("Выберите элемент из списка, кликнув на него.");
		infoForUser.setForeground(Color.white);
		infoForUser.setFont(new Font("Arial", Font.ITALIC, 18));
		infoForUser.setHorizontalAlignment(JLabel.CENTER);	
		windowInfo.add(infoForUser);
		
		// Создание поискового поля
        JTextField searchAllFilms = new JTextField();
        searchAllFilms.setColumns(30);
		JButton filterAllFilms = new JButton("Поиск");
		filterAllFilms.setFont(new Font("Arial", Font.BOLD, 13));
		filterAllFilms.setEnabled(true);
		TableRowSorter<TableModel> sorterAllFilms = new TableRowSorter<TableModel>(model);
		table.setRowSorter(sorterAllFilms);
		JPanel filterPanel = new JPanel();
		filterPanel.setBackground(java.awt.Color.gray);
		filterPanel.add(searchAllFilms);
		filterPanel.add(filterAllFilms);
		
        // создаем панель с таблицей и кнопками
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(windowInfo, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(filterPanel, BorderLayout.SOUTH);
        
        // Действия при пкм.
        JPopupMenu popupMenuFilms = new JPopupMenu();
        JMenuItem deleteFilmFromBD = new JMenuItem("Удалить");
        popupMenuFilms.add(deleteFilmFromBD);
        table.setComponentPopupMenu(popupMenuFilms);
        
        // Слушатель кнопки удаления из общего списка фильмов.
        deleteFilmFromBD.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) { 
				int res = JOptionPane.showConfirmDialog(null,
                        "<html>Вы точно хотите удалить?<br />"
                        + "Абсолютно все сеансы с данным фильмом также будут удалены.<br />Отменить это действие будет нельзя.</html>",
                        "Предупреждение",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        new ImageIcon("./img/warning.png"));
				if (res == JOptionPane.YES_OPTION) {
					int idSelectRow = table.convertRowIndexToModel(table.getSelectedRow());
					Film remMovie = everyFilm.get(idSelectRow);
					for (int i  = 0; i < modelFilms.getRowCount(); i++) {
						if (films.getValueAt(i, 0).equals(remMovie.getTitle()) && 
								films.getValueAt(i, 1).equals(remMovie.getInfo().getDirName() +
										" " + remMovie.getInfo().getDirLastName())) {
							modelFilms.removeRow(i);
							break;
						}
					}
					home.deleteFilmList(remMovie);
					deleteSessionsWithMovieTable(remMovie.getTitle()); 
					em.remove(remMovie);
					model.removeRow(idSelectRow);
					JOptionPane.showMessageDialog(null, "Фильм успешно удален!",
		                    "Ошибка", JOptionPane.INFORMATION_MESSAGE,
		                    new ImageIcon("./img/check.png"));
				}
			}
        });
        
        // Слушатель кнопки поиска в общем списке фильмов.
        filterAllFilms.addActionListener(new ActionListener() {
	          public void actionPerformed(ActionEvent e) {
	        	  log.debug("Нажатие кнопки 'поиск'.");
	        	  String text = searchAllFilms.getText();
	        	  sorterAllFilms.setRowFilter(null);
	        	  sorterAllFilms.setRowFilter(RowFilter.regexFilter(text));
	          }
	     });
        
        // создаем всплывающее окно JOptionPane с панелью
        int result = JOptionPane.showOptionDialog(null, panel, "Выберите значение", 
        		JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, 
        		new ImageIcon("./img/choice.png"), null, null);
        
        if (result == JOptionPane.OK_OPTION) {
        	try {
	        	int rowIndex = table.convertRowIndexToModel(table.getSelectedRow());
	        	String strName = model.getValueAt(rowIndex, 0).toString();
				String strLength = model.getValueAt(rowIndex, 3).toString();
				String strPremiere = model.getValueAt(rowIndex, 4).toString();
				String choiceDir = model.getValueAt(rowIndex, 1).toString();
				String choiceGenre = model.getValueAt(rowIndex, 2).toString(); 
				checkRepeat(strName, choiceDir);
				((DefaultTableModel)films.getModel()).addRow(new String[]{strName, choiceDir, 
				choiceGenre, strLength,strPremiere});
				Info_Film convertDirAndGenre = OperationsWithDatabase.convertInfo(choiceDir, choiceGenre);
				Film newMovie = searchFilm(em, strName, strLength, strPremiere, convertDirAndGenre);
				home.addNewFilmList(newMovie);
				log.info("Добавление нового фильма в таблицу " + strName);
				JOptionPane.showMessageDialog(repertoireWindow, "Фильм успешно добавлен!",
	                    "Успешно", JOptionPane.INFORMATION_MESSAGE,
	                    new ImageIcon("./img/check.png"));
        	} catch (RepeatAddException ex) {
				log.warn("Попытка добавления уже существующего элемента в таблицу.", ex);
				JOptionPane.showMessageDialog(repertoireWindow, ex.getMessage(),
                    "Ошибка", JOptionPane.INFORMATION_MESSAGE,
                    new ImageIcon("./img/warning.png"));
			} catch (IndexOutOfBoundsException ex) {
				log.warn("Попытка добавления фильма без выбора в таблице.", ex);
				JOptionPane.showMessageDialog(repertoireWindow, "Не выбран элемент, в следствие чего список не обновлен.",
	                    "Ошибка", JOptionPane.INFORMATION_MESSAGE,
	                    new ImageIcon("./img/warning.png"));
			}
        }
        Singleton.finishEMandTrans(em);
        
	}
	
	/**
	 * Метод поиска фильма при открытой транзакции.
	 * @param em - интерфейс для работы
	 * @param titleFilm - название искомого фильма
	 * @param lengthFilm - длительность искомого фильма
	 * @param premFilm - премьера искомого фильма
	 * @param info - информация об искомом фильме.
	 * @return найденный фильм
	 */
	private Film searchFilm(EntityManager em, String titleFilm, String lengthFilm, String premFilm, Info_Film info) {
		Query qFilm = em.createQuery("SELECT u FROM Film u WHERE u.title = :title AND u.length = :length "
				+ "AND u.premiere = :premiere AND info_about = :info_about");
		qFilm.setParameter("title", titleFilm);
		qFilm.setParameter("length", Integer.valueOf(lengthFilm));
		qFilm.setParameter("premiere", premFilm);
		qFilm.setParameter("info_about", info);
		Film edFilm = (Film) qFilm.getSingleResult();
		return edFilm;
	}

	/**
	 * Метод создания всплывающей формы заполнения для добавления нового элемента в список.
	 * @return result - выбор пользователя в окне
	 */
	private int addNewElem() {
		int result;
		JPanel infoNewFilm = new JPanel(new GridLayout(5, 3));
		
		infoNewFilm.add(new JLabel("Название фильма: ", 10));
		newMovieName = new JTextField(10);
		infoNewFilm.add(newMovieName);
		
		infoNewFilm.add(new JLabel("ФИ режиссера: ", 10));
		newDir = new JComboBox<String>(allDirectors);
		infoNewFilm.add(newDir);
		
		infoNewFilm.add(new JLabel("Жанр: ", 10));
		newGenre = new JComboBox<String>(allGenres);
		infoNewFilm.add(newGenre);
		
		infoNewFilm.add(new JLabel("Длительность: ", 10));
		newLength = new JTextField(10);
		infoNewFilm.add(newLength);
		
		infoNewFilm.add(new JLabel("Дата премьеры: ", 10));
		newPremiere = new JTextField(10);
		infoNewFilm.add(newPremiere);
		
		result = JOptionPane.showOptionDialog(repertoireWindow, infoNewFilm, "Создание нового объекта", 
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, 
				new ImageIcon("./img/choice.png"), null, null);
		
		return result;
	}

	/**
	 * Перехват ошибки повторного добавления элемента, 
	 * а также некорректного заполнения полей при добавлении элемента.
	 * @param name - название добавляемого фильма.
	 * @param length - длительность добавляемого фильма.
	 * @param premiere - премьера добавляемого фильма.
	 * @param dir - режиссер добавляемого фильма.
	 * @throws RepeatAddException исключение повторного добавления элемента.
	 * @throws EditDigException исключение неправильного ввода полей длительности или даты премьеры.
	 * @throws EmptyFieldException исключение попытки сохранения пустого ввода.
	 */
	public void checkAdd(String name, String length, String premiere, String dir) 
			throws RepeatAddException, EditDigException, EmptyFieldException, WrongDataException {
		int colDir = 1, colName = 0;
		
		// проверка на непустоту полей
		if (name.length() == 0 || length.length() == 0 || premiere.length() == 0)  
			throw new EmptyFieldException();
		
		// проверка на соответствие регулярным выражениям
		if (!length.matches("^[1-9]\\d*$") || !premiere.matches("^\\d{2}.\\d{2}.\\d{4}$")) 
			throw new EditDigException(); 
		
		// проверка введенной даты на реальность
		String[] premSplitString = premiere.split("\\.");
		int[] premSplit = new int[] {Integer.valueOf(premSplitString[0]), Integer.valueOf(premSplitString[1]), Integer.valueOf(premSplitString[2])};
		int flag = 0;
		if (premSplit[1] > 0 && premSplit[1] <= 12 && premSplit[0] > 0 && premSplit[0] <= 31 && premSplit[2] >= 1888) {
			if (premSplit[1] == 2 && (premSplit[0] < 29 || ((((premSplit[2] % 4) == 0 && 
					(premSplit[2] % 100) != 0) || (premSplit[2] % 400) == 0) && premSplit[0] < 30))) 
				flag = 1;
	        else if ((premSplit[1] == 4 || premSplit[1] == 6 || premSplit[1] == 9 || premSplit[1] == 11) 
	        		&& premSplit[0] < 31) 
	        	flag = 1;
	        else if (premSplit[1] == 1 || premSplit[1] == 3 || premSplit[1] == 5 || premSplit[1] == 7 || premSplit[1] == 8 || premSplit[1] == 10 || premSplit[1] == 12) 
	        	flag = 1;
		}
		if (flag == 0) 
			throw new WrongDataException();
		
		// проверка на то, чтобы элемент не был повторяющимся
		checkRepeat(name, dir);
				
	}
	
	/**
	 * Метод перехвата повторного ввода фильма.
	 * @param name - название фильма
	 * @param dir - режиссер фильма
	 * @throws RepeatAddException  - исключение повторного добавления.
	 */
	public void checkRepeat(String name, String dir) throws RepeatAddException {
		for (int i = 0; i < films.getRowCount(); i++) {
			if (films.getModel().getValueAt(i, 0).equals(name)  
					&& films.getModel().getValueAt(i, 1).equals(dir)) {
				throw new RepeatAddException();
			}
		}
	}
	
	/**
	 * Перехват ошибок ввода в редактируемое поле таблицы films.
	 * @param colNumb - номер столбца, ячейка в котором редактируется.
	 * @param newMovieName - новое значение ячейки.
	 * @throws EditDigException - исключение некорректного ввода.
	 * @throws EmptyFieldException - исключение пустого ввода.
	 */ 
	public void checkEdit(int colNumb, String newField) throws EditDigException, EmptyFieldException {
		if (newField.length() == 0) 
			throw new EmptyFieldException();
		if (colNumb == 3 && !newField.matches("^[1-9]\\d*$")) 
			throw new EditDigException();
		if (colNumb == 4 && !newField.matches("^\\d{2}.\\d{2}.\\d{4}$")) 
			throw new EditDigException();
	}
	
	/**
	 * Перехват ошибки отсутствия выбора столбца.
	 * @param selected - выбранное в JComboBox значение.
	 * @throws SearchQueryException исключение.
	 */
	public void checksearchQueryField(Object selected)  throws SearchQueryException {
		if ("Столбец".equals(selected)) throw new SearchQueryException();
	}
	
	
	/**
	 * Класс исключения отсутствия выбора столбца.
	 */
	public class SearchQueryException extends Exception {
		public SearchQueryException() {
			super("Выберите столбец для поиска!");
		}
	}
	
	/**
	 * Класс исключения повторного добавления элемента.
	 */
	public class RepeatAddException extends Exception {
		public RepeatAddException() {
			super("Фильм с таким названием от этого режиссера уже есть!");
		}
	}
	
	/**
	 * Класс исключения некорректно введенных данных.
	 */
	public class EditDigException extends Exception {
		public EditDigException() {
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
	
	/**
	 * Класс исключения ввода несуществующей даты.
	 */
	public class WrongDataException extends Exception {
		public WrongDataException() {
			super("Убедитесь, что Вы ввели реальную дату.");
		}
	}
	
}
