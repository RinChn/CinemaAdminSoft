package sp.entities;

import jakarta.persistence.*;

/**
 * Класс-сущность для работы с таблицей информации о фильмах (режиссеры и жанры).
 */
@Entity
@Table(name="info_film")
public class Info_Film {
	/** id информации в таблице **/
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	/** Имя режиссера **/
	@Column(name="name")
	private String name;
	/** Фамилия режиссера **/
	@Column(name="last_name")
	private String last_name;
	/** Жанр **/
	@Column(name="genre")
	private String genre;
	
	/**
	 * Конструктор
	 */
	public Info_Film(String dir_name, String dir_last_name, String genre) {
		this.name = dir_name;
		this.last_name = dir_last_name;
		this.genre = genre;
	}
	
	/**
	 * Дефолтный конструктор класса.
	 */
	public Info_Film() {
		this.name = "";
		this.last_name = "";
		this.genre = "";
	}
	
	/** 
	 * Геттер id в таблице.
	 * @return id
	 */
	public int getIdDir() {
		return id;
	}
	
	/** 
	 * Геттер имени режиссера.
	 * @return name
	 */
	public String getDirName() {
		return name;
	}
	
	/**
	 * Сеттер имени режиссера.
	 * @param new_name - новое имя режиссера.
	 */
	public void setDirName(String new_name) {
		this.name = new_name;
	}
	
	/** 
	 * Геттер фамилии режиссера.
	 * @return last_name
	 */
	public String getDirLastName() {
		return last_name;
	}
	
	/**
	 * Сеттер фамилии режиссера.
	 * @param new_ln - новая фамилия режиссера.
	 */
	public void setDirLastName(String new_ln) {
		this.last_name = new_ln;
	}

	/** 
	 * Геттер жанра.
	 * @return genre
	 */
	public String getGenre() {
		return genre;
	}
	
	/**
	 * Сеттер жанра.
	 * @param new_genre - новый жанр.
	 */
	public void setGenre(String new_genre) {
		this.genre = new_genre;
	}
}