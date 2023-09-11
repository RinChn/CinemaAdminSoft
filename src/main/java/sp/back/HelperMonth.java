package sp.back;

/**
 * Класс-помощник для получения названий месяцев.
 */
public class HelperMonth {
	/** Названия месяцев на русском в именительном падеже **/
	private static final String[] monthNames = {"Январь", "Февраль", "Март", "Апрель", "Май", 
			"Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
	
	/** Названия месяцев на русском в родительном падеже **/
	private static final String[] monthGenetiveNames = {"Января", "Февраля", "Марта", "Апреля", "Мая", 
			"Июня", "Июля", "Августа", "Сентября", "Октября", "Ноября", "Декабря"};
	
	/** Названия месяцев на английском **/
	private static final String[] englNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
	
	/**
	 * Метод получения названия месяца в именительном падеже.
	 * @param monthNumb - номер месяца при счислении от 0
	 * @return название месяца
	 */
	public static String getName(int monthNumb) {
		return monthNames[monthNumb - 1];
	}
	
	/**
	 * Метод получения названия месяца в родительном падеже
	 * @param monthNumb - номер месяца при счислении от 0
	 * @return название месяца
	 */
	public static String GenitiveCase(int monthNumb) {
		return monthGenetiveNames[monthNumb - 1];
	}
	
	/**
	 * Метод получения названия месяца на английском.
	 * @param monthNumb - номер месяца при счислении от 0
	 * @return название месяца
	 */
	public static String getEnglishName(int monthNumb) {
		return englNames[monthNumb - 1];
	}
}
