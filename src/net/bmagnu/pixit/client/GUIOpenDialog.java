package net.bmagnu.pixit.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import net.bmagnu.pixit.common.PiXitImage;

public class GUIOpenDialog extends Dialog<GUIOpenDialogResult> {
	
	private TextField ip;
	
	private TextField name;
	
	private CheckBox caching;
	
	public GUIOpenDialog(String defaultIp) {
		GridPane grid = new GridPane();
		grid.setHgap(5);
		grid.setVgap(10);
		
		caching = new CheckBox("Cache Images?");
		caching.setSelected(false);
		
		Region cacheSpace = new Region();
		
		Button cacheFolder = new Button("Pre-Cache Folder...");
		cacheFolder.setStyle("-fx-font-size: 8.5px;");
		cacheFolder.setOnAction((event) -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			File selectedDirectory = directoryChooser.showDialog(getDialogPane().getScene().getWindow());
			if(selectedDirectory == null || !selectedDirectory.exists() || !selectedDirectory.isDirectory())
				return;
			
			try {
				cacheDirectory(selectedDirectory);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		HBox cacheBox = new HBox(10, caching, cacheSpace, cacheFolder);
		HBox.setHgrow(cacheSpace, Priority.ALWAYS);
		cacheBox.setAlignment(Pos.CENTER_LEFT);
		
		VBox box = new VBox(13, grid, cacheBox);
		
		final DialogPane dialogPane = getDialogPane();
		dialogPane.setContent(box);
		
		dialogPane.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
		
		ip = new TextField(defaultIp);
		name = new TextField("");
		
		grid.add(new Label("Server IP: "), 0, 0);
		grid.add(new Label("User Nickname: "), 0, 1);
		
		grid.add(ip, 1, 0);
		grid.add(name, 1, 1);
		
		setResultConverter((dialogButton) -> {
			GUIOpenDialogResult data = new GUIOpenDialogResult();
			
			data.ip = ip.getText();
			data.name = name.getText();
			data.caching = caching.isSelected();
			
			ButtonData button = dialogButton == null ? null : dialogButton.getButtonData();
			return button == ButtonData.OK_DONE ? data : null;
		});
	}
	
	private int collisionCount = 0;
	
	private void cacheDirectory(File directory) throws IOException {
		System.out.println("Caching: " + directory.getAbsolutePath());
		System.out.println();
		
		Files.walk(directory.toPath()).filter((file) -> {
			String mime;
			try {
				mime = Files.probeContentType(file);
				return mime != null && mime.split("/")[0].equals("image");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}).forEachOrdered((file) -> {
			String hash = "";
			try {
				hash = PiXitImage.makeHash(file.toFile());
				
				Path copied = Paths.get("./cache/" + hash);
			    Files.copy(file, copied);
			} catch (IOException e) {
				collisionCount++;
			}
		});
		
		System.out.println("Cached Folder");
		System.out.println(collisionCount + " already Existed");
	}
}

class GUIOpenDialogResult{
	public String ip;
	
	public String name;
	
	public boolean caching;
}
