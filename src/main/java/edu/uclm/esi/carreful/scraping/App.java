package edu.uclm.esi.carreful.scraping;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import edu.uclm.esi.carreful.dao.ProductDao;
import edu.uclm.esi.carreful.model.Product;

/**
 * Hello world!
 *
 */
@Component
public class App 
{
	@Autowired
	private ProductDao productDao;
	
	@EventListener(ContextRefreshedEvent.class)
	public void charge()
	{
		Path path = FileSystems.getDefault().getPath("src/main/java/edu/uclm/esi/carreful/scraping/chromedriver.exe");       
		System.setProperty("webdriver.chrome.driver", path.toString());
		
		//System.setProperty("webdriver.chrome.driver", 
		//		"C:\\Users\\ramon\\Downloads\\chromedriver.exe");

		ChromeOptions options = new ChromeOptions();
		options.addArguments("headless");
		options.addArguments("window-size=1400,800");       
		options.addArguments("disable-gpu");
	
		WebDriver driver = new ChromeDriver(options);   

		driver.get("https://www.carrefour.es/supermercado/el-mercado/cat20002/c");

		WebElement botonCookies = driver.findElement(By.xpath("/html/body/div[3]/div/footer/div[1]/div/button"));
		botonCookies.click();
		WebElement divPaginas = driver.findElement(By.className("pagination__main"));
		String texto = divPaginas.getText().trim();
		int posTercerEspacio = texto.lastIndexOf(' ');
		texto = texto.substring(posTercerEspacio).trim();

		int paginas = Integer.parseInt(texto);
		WebElement siguiente;

		String home = System.getProperty("user.home");
		try(FileOutputStream fos = new FileOutputStream(home + "/productos.json.text")) {
			fos.write("[".getBytes());
			procesarPagina(driver,fos);
			siguiente = driver.findElement(By.className("pagination__row"));
			siguiente = siguiente.findElement(By.tagName("a"));
			siguiente.click();

			try{
				Thread.sleep(1000);
			}catch(InterruptedException e){
			}

			for (int i=2; i<paginas; i++) {
				procesarPagina(driver,fos);
				driver.findElement(By.cssSelector(".pagination__next")).click();

				try{
					Thread.sleep(1000);
				}catch(InterruptedException e){
				}
			}


		}catch(Exception e){
		
		}


	}

	private void procesarPagina(WebDriver driver, FileOutputStream fos) throws IOException {
		List<WebElement> divProductos = driver.findElements(By.className("product-card__parent"));
		for (WebElement divProducto : divProductos) {
			try {
				List<WebElement> spanesPrecio = divProducto.findElements(By.className("product-card__price"));
				String precio;
				WebElement spanPrecio;

				if(spanesPrecio.size()>0) {
					spanPrecio = divProducto.findElement(By.className("product-card__price"));
				}else {
					spanPrecio = divProducto.findElement(By.className("product-card__price--current"));
				}

				precio = spanPrecio.getText();
				WebElement h2 = divProducto.findElement(By.tagName("h2"));
				String nombre = h2.getText();

				//            	JSONObject jso = new JSONObject();
				//            	jso.put("nombre", nombre);
				//            	jso.put("precio", precio);           	
				//            	fos.write(jso.toString().getBytes());
				saveProduct(nombre, precio);
			} catch (Exception e) {
				
			}
		}
	}
	private void saveProduct(String nombre, String precio) {
		Product prod = new Product();
		prod.setNombre(nombre);
		prod.setPrecio(precio);
		prod.setCodigo(UUID.randomUUID().toString());
		productDao.save(prod);
	}

}


