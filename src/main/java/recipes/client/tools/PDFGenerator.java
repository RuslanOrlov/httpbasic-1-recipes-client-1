package recipes.client.tools;

import java.awt.Color;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;
import recipes.client.dtos.IngredientDTO;
import recipes.client.dtos.Recipe;

//import lombok.extern.slf4j.Slf4j;

//@Slf4j
public class PDFGenerator {
	
	public void generate(HttpServletResponse response, List<Recipe> recipes, ReportType type) 
							throws IOException, DocumentException {
		
		// Создать объект документа форматом А4 
		Document document = new Document(PageSize.A4);
		
		// Получить объект PdfWriter, который должен записывать 
		// в указанный документ и через указанный выходной поток 
		// объекта HttpServletResponse 
		PdfWriter.getInstance(document, response.getOutputStream());
		
		// Открыть документ 
		document.open();
		
		// Создать шрифт с поддержкой русского языка 
		/*
		 * Скачать файлы шрифтов с поддержкой русского языка можно с ресурса: 
		 * - https://fonts-online.ru/languages/russian
		 * */
		BaseFont bFont = BaseFont.createFont(
					"src/main/resources/static/fonts/timesnrcyrmt.ttf", 
					BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		Font myFont = new Font(bFont, 14);
		
		// Создать параграф с целью использования в качестве заголовка отчета 
		String title = (type == ReportType.LIST_TYPE ? "СПИСОК РЕЦЕПТОВ" : "КАРТОЧКА РЕЦЕПТА");
		Paragraph paragraph = new Paragraph(title, myFont);
		
		// Выровнять параграф по центру и установить отступы перед параграфом и после него
		paragraph.setAlignment(Paragraph.ALIGN_CENTER);
		paragraph.setSpacingBefore(20);
		paragraph.setSpacingAfter(20);
		
		// Добавить созданный параграф (в роли заголовка) в документ 
		document.add(paragraph);
		
		// Создать таблицу для заполнения отчета по рецептам из 3-х столбцов 
		// (что равно количеству полей сущности Recipe (Рецепт))
		PdfPTable recipeTable = createTableForRecipe();
		
		// Создать ячейку для заполнения шапки (заголовка) таблицы
		PdfPCell cell = new PdfPCell(); 
		
		// Установить свойства ячейки: 
		// - отступ текста ячейки от границ ячейки 
		// - горизонтальное выравнивание текста в ячейке 
		// - цвет фона ячейки 
		cell.setPadding(5);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(Color.MAGENTA);
		
		//Сформировать шапку (заголовок) таблицы
		cell.setPhrase(new Phrase("id", myFont));
		recipeTable.addCell(cell);
		cell.setPhrase(new Phrase("Название", myFont));
		recipeTable.addCell(cell);
		cell.setPhrase(new Phrase("Описание", myFont));
		recipeTable.addCell(cell);
		
		// Добавить в документ шапку таблицы 
		document.add(recipeTable);
		
		// Создать  форматтер для форматирования даты и времени в нужный формат 
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		
		// Изменить свойства ячейки для добавления через нее данных об ингредиентах: 
		// - отступ текста ячейки от границ ячейки 
		// - цвет фона ячейки 
		cell.setPadding(2);
		cell.setBackgroundColor(Color.LIGHT_GRAY);
		
		// Пройти по коллекции рецептов и построчно добавить их в таблицу
		for (Recipe recipe : recipes) {
			// Пересоздать таблицу для заполнения строки текущего рецепта
			recipeTable = createTableForRecipe();
			// Сформировать в таблице строку текущего рецепта 
			recipeTable.addCell(new Phrase(recipe.getId().toString(), myFont));
			recipeTable.addCell(new Phrase(recipe.getName(), myFont));
			recipeTable.addCell(new Phrase(recipe.getDescription(), myFont));
			// Добавить в документ таблицу со строкой текущего рецепта 
			document.add(recipeTable);
			
			// Создать таблицу для заполнения отчета по ингредиентам рецепта из 2-х 
			// столбцов (что равно количеству полей сущности Ingredients (Ингредиент))
			PdfPTable ingredientsTable = createTableForIngredients();
			
			// Пройти по коллекции ингредиентов текущего рецепта и добавить их в отдельную таблицу
			for (IngredientDTO ingredientDTO : recipe.getIngredients()) {
				// Заполнить таблицу строками ингредиентов текущего рецепта и одновременно 
				// измененить горизонтальное выравнивание текста в ячейках разных столбцов 
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPhrase(new Phrase(ingredientDTO.getId().toString(), myFont));
				ingredientsTable.addCell(cell);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setPhrase(new Phrase(ingredientDTO.getName(), myFont));
				ingredientsTable.addCell(cell);
			}
			// Добавить в документ таблицу со строками ингредиентов текущего рецепта 
			document.add(ingredientsTable);
		}
		// Добавить созданную талицу в документ
		//document.add(recipeTable);
		
		// Создать параграф как подпись документа 
		paragraph = new Paragraph("Дата и время выгрузки: " + 
									LocalDateTime.now().format(formatter), myFont);
		// Добавить параграф (в роли подписи) в документ 
		document.add(paragraph);
		
		// Закрыть документ 
		document.close();
	}
	
	private PdfPTable createTableForRecipe() {
		// Создать таблицу для заполнения отчета по рецептам из 3 столбцов 
		// (что равно количеству полей сущности Recipe (Рецепт))
		PdfPTable table = new PdfPTable(3);
		
		// Установить свойства таблицы для рецептов: 
		// - ширинна таблицы в процентах от пространства страницы 
		// - ширина столбцов относительно друг друга (целочисленный массив) 
		// - отступ перед таблицей сверху (отделяет от элементов сверху) 
		// - отступ после таблицей снизу (отделяет от элементов снизу) 
		table.setWidthPercentage(100);
		table.setWidths(new int[] {2, 6, 9});
		table.setSpacingBefore(0);
		table.setSpacingAfter(0);
		
		return table;
	}
	
	private PdfPTable createTableForIngredients() {
		// Создать таблицу для заполнения отчета по ингредиентам 
		// рецепта из 2 столбцов (что равно количеству полей 
		// сущности Ingredients (Ингредиент))
		PdfPTable table= new PdfPTable(2);
		
		// Установить свойства таблицы для ингредиентов: 
		// - ширинна таблицы в процентах от пространства страницы 
		// - ширина столбцов относительно друг друга (целочисленный массив) 
		// - отступ перед таблицей сверху (отделяет от элементов сверху) 
		// - отступ после таблицей снизу (отделяет от элементов снизу) 
		table.setWidthPercentage(100);
		table.setWidths(new int[] {2, 15});
		table.setSpacingBefore(0);
		table.setSpacingAfter(0);
		
		return table;
	}
}
