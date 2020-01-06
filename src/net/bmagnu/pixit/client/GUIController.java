package net.bmagnu.pixit.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class GUIController {
	
	@FXML 
	private ImageView img1, img2, img3, img4, img5, img6, img7;	
	
	private ImageView[] imageSlots;
	private Integer[] imageId;
	
	private Map<Integer, Image> imageCache = new HashMap<>();
	
	@FXML
	private VBox czarBox;
	
	@FXML
	private Label czarTheme;
	
	@FXML
	private TextField czarThemeTextfield;
	
	@FXML
	private Label pointsLabel;
	
	@FXML 
	protected void handleCzarThemeSubmit(ActionEvent event) {
		//Clicked Submit Theme Button
		Client.instance.proxy.playCzarTheme(czarThemeTextfield.getText());
	}
	
	@FXML 
	protected void handleOnImageClicked(MouseEvent event) {
		Object obj = event.getSource();

        if ( obj instanceof StackPane ) //Probably an Image
        {
        	Integer imageSlot = Integer.parseInt((String) ((StackPane) obj).getUserData()) - 1;
        	System.out.println("Clicked on Image " + imageSlot);
        	
        	switch(Client.instance.state) {
				case STATE_WAITING_FOR_CARDS :
					Client.instance.proxy.playImage(imageSlot);
					break;
				case STATE_WAITING_FOR_GUESS :
					Client.instance.proxy.playImageGuess(imageId[imageSlot]);
					break;
				case STATE_WAITING_FOR_CZAR :
				case STATE_WAITING_FOR_CZAR_YOU :
				case STATE_WAITING_FOR_GUESS_CZAR:
					System.out.println("Can't click Images as Czar / while Czar is choosing the Theme");
					break;
        	}
        }
	}
	
	public void initialize() {
		imageSlots = new ImageView[]{img1, img2, img3, img4, img5, img6, img7};
		imageId = new Integer[]{-1, -1, -1, -1, -1, -1, -1};
	}
	
	public void showCzarBox(boolean show) {
		czarBox.setVisible(show);
		czarTheme.setVisible(!show);
	}
	
	public void setCzarTheme(String theme) {
		czarTheme.setText(theme);
	}
	
	public void setPoints(Integer points) {
		pointsLabel.setText("Points: " + points);
	}
	
	public void setNewImages(List<Integer> imageId) {
		for(int i = 0; i < 7; i++) {
			if(imageId.size() <= i || imageId.get(i) == null) {
				//No Img
				imageSlots[i].setVisible(false);
			}
			else if(imageId.get(i) != this.imageId[i]) {
				//New Img
				imageSlots[i].setVisible(true);
				
				this.imageId[i] = imageId.get(i);
				Image image = imageCache.get(imageId.get(i));
				if(image == null) {
					//Img was not cached
					image = Client.instance.proxy.loadImage(imageId.get(i));
					imageCache.put(imageId.get(i), image);
				}
				
				imageSlots[i].setImage(image);
			}
			//else {
				//Keep Img
			//}
		}
	}
	
	public void setNewImages(Map<Integer, Integer> imageId) {
		for(int i = 0; i < 7; i++) {
			if(imageId.size() <= i || imageId.get(i) == null) {
				//No Img
				imageSlots[i].setVisible(false);
			}
			else if(imageId.get(i) != this.imageId[i]) {
				//New Img
				imageSlots[i].setVisible(true);
				
				this.imageId[i] = imageId.get(i);
				Image image = imageCache.get(imageId.get(i));
				if(image == null) {
					//Img was not cached
					image = Client.instance.proxy.loadImage(imageId.get(i));
					imageCache.put(imageId.get(i), image);
				}
				
				imageSlots[i].setImage(image);
			}
			//else {
				//Keep Img
			//}
		}
	}
}
