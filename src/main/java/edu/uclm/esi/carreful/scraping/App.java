package edu.uclm.esi.carreful.scraping;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.commons.codec.binary.Base64;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import edu.uclm.esi.carreful.dao.CategoriaDao;
import edu.uclm.esi.carreful.dao.ProductDao;
import edu.uclm.esi.carreful.model.Categoria;
import edu.uclm.esi.carreful.model.Product;

@Component
public class App
{	
	private static final String SCROLL = "window.scrollTo(0, 2000)";
	
	@Autowired
	private ProductDao productDao;
	@Autowired
	private CategoriaDao categoriaDao;

	private Random rn = new Random();

	//@EventListener(ContextRefreshedEvent.class)
	public void chargechar() {
		List<CompletableFuture<String>> futuresList = new ArrayList<>();
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
		CompletableFuture<Void> completableFuture = allFutures.toCompletableFuture();
		try {
			completableFuture.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			 Thread.currentThread().interrupt();
		}

	}
	@Async("threadPoolExecutor")
	public String charge(String url, String cate) 
	{
		Path path = FileSystems.getDefault().getPath("src/main/java/edu/uclm/esi/carreful/scraping/chromedriver.exe");       
		System.setProperty("webdriver.chrome.driver", path.toString());

		ChromeOptions options = new ChromeOptions();
		options.addArguments("headless");
		options.addArguments("--start-maximized");
		options.addArguments("disable-gpu");

		WebDriver driver = new ChromeDriver(options);   

		driver.get(url);
		WebElement botonCookies = driver.findElement(By.xpath("/html/body/div[3]/div/footer/div[1]/div/button"));
		botonCookies.click();
		WebElement divPaginas = driver.findElement(By.className("pagination__main"));
		String texto = divPaginas.getText().trim();
		int posTercerEspacio = texto.lastIndexOf(' ');
		texto = texto.substring(posTercerEspacio).trim();

		int paginas = Integer.parseInt(texto);
		WebElement siguiente;
		Optional<Categoria> opCategoria = categoriaDao.findByNombre(cate);
		Categoria categoria;
		if(opCategoria.isPresent()) {
			categoria = opCategoria.get();
		}else {
			categoria = new Categoria();
			categoria.setNombre(cate);
			categoriaDao.save(categoria);
		}
		procesarPagina(driver, categoria);
		siguiente = driver.findElement(By.className("pagination__row"));
		siguiente.findElement(By.tagName("a")).click();

		for (int i=1; i<paginas; i++) {
			procesarPagina(driver, categoria);
			driver.findElement(By.cssSelector(".pagination__next")).click();
		}

		categoriaDao.save(categoria);
		driver.close();
		driver.quit();

		return "Productos cargados de "+cate;


	}

	private void procesarPagina(WebDriver driver, Categoria categoria) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(SCROLL);
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		js.executeScript(SCROLL);
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		js.executeScript(SCROLL);
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		WebElement divCardList = driver.findElement(By.className("product-card-list"));
		List<WebElement> divProductos = divCardList.findElements(By.className("product-card__parent"));
		for (WebElement divProducto : divProductos) {
			try {
				WebElement h2 = divProducto.findElement(By.tagName("h2"));
				String nombre = h2.getText();
					List<WebElement> spanesPrecio = divProducto.findElements(By.className("product-card__price"));
					String precio;
					WebElement spanPrecio;
	
					if(spanesPrecio.size()>0) {
						spanPrecio = divProducto.findElement(By.className("product-card__price"));
					}else {
						spanPrecio = divProducto.findElement(By.className("product-card__price--current"));
					}
	
					precio = spanPrecio.getText().replace("€", "").replace(",", ".");
					
					Boolean congelado = false;
					congelado = isCongelado(divProducto);
					WebElement img = divProducto.findElement(By.tagName("img"));
					String urlImage = img.getAttribute("src");   
	
					String base64F = convertUrlBase64(urlImage);
					Product prod = new Product();
					prod.setNombre(nombre);
					prod.setPrecio(Double.parseDouble(precio));
					prod.setCategoria(categoria);
					prod.setStock(rn.nextInt(20));
					prod.setCongelado(congelado);
					prod.setFoto("data:image/jpg;base64, "+base64F);
					if(base64F!=null) {
						productDao.save(prod);
						categoria.addProd();
					}
			} catch (Exception e) {//Se salta este paso 
			}
		}

	}

	private String convertUrlBase64(String imageUrl)  {
		try {
			URL imageUrl1 = new URL(imageUrl);
			URLConnection ucon = imageUrl1.openConnection();
			InputStream is = ucon.getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int read = 0;
			while ((read = is.read(buffer, 0, buffer.length)) != -1) {
				baos.write(buffer, 0, read);
			}
			baos.flush();
			return Base64.encodeBase64String(baos.toByteArray());
		} catch (Exception e) {//Se salta este paso 
		}
		return null;
	}
	
	private boolean isCongelado(WebElement divProducto) {
		try {
		WebElement ol = divProducto.findElement(By.className("product-card__info-tag"));
		String con = ol.getText();
		if(con.equals("CONGELADO")) {
			return true;
		}
		}catch (Exception e) {
			return false;
		}
		return false;
	}

}


