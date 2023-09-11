package sp.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

/**
 * Главный класс программы, запускающий главный экран.
 * @author Арина Морик
 *
 */
public class AppMain {
	/** Номер месяца, который открывается при запуске программы **/
	private static final int NUMB_START_MONTH = 5;
	
	/** Экранная форма месячного плана. **/
	private MonthlyPlan mainPage;
	 
	/** Номер месяца, расписание сеансов на который открывается в качестве домашней страницы **/
	private int monthNumbNow = NUMB_START_MONTH;

	/** Логгер класса **/
	private static final Logger log = Logger.getLogger(AppMain.class.getName());
	
	/**
	 * Конструктор класса.
	 */
	public AppMain() { 
		mainPage = new MonthlyPlan(NUMB_START_MONTH);
		backAndFor();
	}
	
	/**
	 * Запуск основного приложения, начиная с экранной формы сеансов на месяц.
	 */
	public static void main(String[] args) {
		log.info("Запуск приложения.");
		AppMain app = new AppMain();
		app.mainPage.show();
	}
	
	/**
	 * Слушатели для кнопок 
	 */
	private void backAndFor() {
		
		// Если сейчас открыт январь, то кнопка для переключения на предыдущий месяц становится недоступной.
		if (monthNumbNow != 1)
			// Назначение слушателя кнопке переключения на предыдущий сеанс, закрытие старого в случае нажатия.
			mainPage.previous.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					try {
						updatePages(-1);
					} catch (WrongMonthException ex) {
						log.warn("Пользователем совершено двойное нажатие", ex);
						JOptionPane.showMessageDialog(null, ex.getMessage(),
			                    "Ошибка", JOptionPane.INFORMATION_MESSAGE,
			                    new ImageIcon("./img/warning.png"));
					}
				}
			});
		else 
			mainPage.previous.setEnabled(false);
		
		// Если сейчас открыт декабрь, то кнопка для переключения на следующий месяц становится недоступной.
		if (monthNumbNow != 12)
			// Назначение слушателя кнопке переключения на следующий сеанс, закрытие старого в случае нажатия.
			mainPage.next.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					try {
						updatePages(1);
					} catch (WrongMonthException ex) {
						log.warn("Пользователем совершено двойное нажатие", ex);
						JOptionPane.showMessageDialog(null, ex.getMessage(),
			                    "Ошибка", JOptionPane.INFORMATION_MESSAGE,
			                    new ImageIcon("./img/warning.png"));
					}
				}
			});
		else 
			mainPage.next.setEnabled(false);
	}
	
	/**
	 * Обновление актуального экрана сеансов.
	 * @param flag - число, суммирование с которым необходимо для переключения месяца.
	 * @throws WrongMonthException - исключение,
	 *  предотвращающее двойное нажатие на клавишу следующий/предыдущий в случае, когда открыты январь или декабрь.
	 */
	private void updatePages(int flag) throws WrongMonthException {
		if ((monthNumbNow + flag) == 0 || (monthNumbNow + flag) == 13)
			throw new WrongMonthException();
		else
			monthNumbNow += flag;
		log.info("Переключение на месяц " + monthNumbNow);
		MonthlyPlan oldPage = mainPage;
		mainPage = new MonthlyPlan(monthNumbNow);
		mainPage.show();
		oldPage.close();
		backAndFor();
	}
	
	/** 
	 * Вызов исключения переключения на некорректный месяц.
	 */
	private class WrongMonthException extends Exception {
		public WrongMonthException() {
			super("Дальнейшее перелистывание невозможно!");
		}
	}
}
