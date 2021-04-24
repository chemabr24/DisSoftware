package edu.uclm.esi.carreful.scraping;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import edu.uclm.esi.carreful.dao.CategoriaDao;
import edu.uclm.esi.carreful.dao.ProductDao;
import edu.uclm.esi.carreful.model.Categoria;
import edu.uclm.esi.carreful.model.Product;

/**
 * Hello world!
 *
 */
@Component
public class App
{
	private static final Log LOG = LogFactory.getLog(App.class);

	@Autowired
	private ProductDao productDao;
	@Autowired
	private CategoriaDao categoriaDao;

	private Random rn = new Random();

	@EventListener(ContextRefreshedEvent.class)
	public void chargechar() {
		List<CompletableFuture<String>> futuresList = new ArrayList<CompletableFuture<String>>();
		CompletableFuture<String> cat1 = CompletableFuture.supplyAsync(()->(charge("https://www.carrefour.es/supermercado/el-mercado-carniceria/F-10flZ12bl/c", "Carniceria")));
		CompletableFuture<String> cat2 = CompletableFuture.supplyAsync(()->(charge("https://www.carrefour.es/supermercado/el-mercado-charcuteria/F-10flZ10fn/c", "Charcuteria")));
		CompletableFuture<String> cat3 = CompletableFuture.supplyAsync(()->(charge("https://www.carrefour.es/supermercado/el-mercado-frutas/F-10flZ10x4/c", "Fruta")));
		CompletableFuture<String> cat4 = CompletableFuture.supplyAsync(()->(charge("https://www.carrefour.es/supermercado/el-mercado-panaderia-bolleria-y-pasteleria/F-10flZ10gq/c", "Pan y bollos")));
		CompletableFuture<String> cat5 = CompletableFuture.supplyAsync(()->(charge("https://www.carrefour.es/supermercado/el-mercado-pescaderia/F-10flZ11fg/c", "Pescado")));
		CompletableFuture<String> cat6 = CompletableFuture.supplyAsync(()->(charge("https://www.carrefour.es/supermercado/el-mercado-quesos/F-10flZ11nx/c", "Queso")));
		CompletableFuture<String> cat7 = CompletableFuture.supplyAsync(()->(charge("https://www.carrefour.es/supermercado/el-mercado-sushi-del-dia/F-10flZ12bf/c", "Sushi")));

		futuresList.add(cat1);
		futuresList.add(cat2);
		futuresList.add(cat3);
		futuresList.add(cat4);
		futuresList.add(cat5);
		futuresList.add(cat6);
		futuresList.add(cat7);


		CompletableFuture<Void> allFutures = CompletableFuture
				.allOf(futuresList.toArray(new CompletableFuture[futuresList.size()]));
		CompletableFuture<List<String>> allCompletableFuture = allFutures.thenApply(future -> {
			return futuresList.stream().map(completableFuture -> completableFuture.join())
					.collect(Collectors.toList());	
		});
		CompletableFuture<List<String>> completableFuture = allCompletableFuture.toCompletableFuture();
		try {
			List<String> finalList = (List<String>) completableFuture.get();
			LOG.info(finalList);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

	}
	@Async("threadPoolExecutor")
	public String charge(String url, String cate) 
	{
		Path path = FileSystems.getDefault().getPath("src/main/java/edu/uclm/esi/carreful/scraping/chromedriver.exe");       
		System.setProperty("webdriver.chrome.driver", path.toString());

		ChromeOptions options = new ChromeOptions();
		options.addArguments("headless");
		options.addArguments("window-size=1400,800");       
		options.addArguments("disable-gpu");

		WebDriver driver = new ChromeDriver(options);   

		driver.get(url);
		//driver.get("https://www.carrefour.es/supermercado/el-mercado/cat20002/c");
		WebElement botonCookies = driver.findElement(By.xpath("/html/body/div[3]/div/footer/div[1]/div/button"));
		botonCookies.click();
		WebElement divPaginas = driver.findElement(By.className("pagination__main"));
		String texto = divPaginas.getText().trim();
		int posTercerEspacio = texto.lastIndexOf(' ');
		texto = texto.substring(posTercerEspacio).trim();

		int paginas = Integer.parseInt(texto);
		WebElement siguiente;

		Categoria categoria = new Categoria();
		categoria.setNombre(cate);
		categoriaDao.save(categoria);

		procesarPagina(driver, categoria);
		siguiente = driver.findElement(By.className("pagination__row"));
		siguiente = siguiente.findElement(By.tagName("a"));
		siguiente.click();

		for (int i=1; i<paginas; i++) {
			procesarPagina(driver, categoria);
			driver.findElement(By.cssSelector(".pagination__next")).click();
		}


		categoriaDao.save(categoria);
		driver.close();
		driver.quit();
		
		return "Productos cargados de categoria "+cate;


	}

	private void procesarPagina(WebDriver driver, Categoria categoria) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, 1000)");
        try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
		}
        js.executeScript("window.scrollTo(0, 1500)");
        try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
		}
        js.executeScript("window.scrollTo(0, 2000)");
        try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
		}
        WebElement divCardList = driver.findElement(By.className("product-card-list"));
		List<WebElement> divProductos = divCardList.findElements(By.className("product-card__parent"));
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
				Boolean congelado = false;
				try {
					WebElement ol = divProducto.findElement(By.className("product-card__info-tag"));
					String con = ol.getText();
					if(con.equals("CONGELADO")) {
						congelado = true;
					}
				} catch (Exception e) {
					
				}
				saveProduct(nombre, precio, categoria, congelado);
			} catch (Exception e) {

			}
		}
	}

	private void saveProduct(String nombre, String precio, Categoria categoria, Boolean congelado) {
		Product prodex = productDao.findByNombre(nombre);
		if(prodex==null) {
			Product prod = new Product();
			prod.setNombre(nombre);
			String p = precio.replace("€", "").replace(",", ".");
			Double prec = Double.parseDouble(p);
			prod.setPrecio(prec);
			prod.setCategoria(categoria);
			prod.setStock(rn.nextInt(20));
			prod.setCongelado(congelado);
			productDao.save(prod);
			categoria.addProd();
		}
	}

}


