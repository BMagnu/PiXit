package net.bmagnu.pixit.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.bmagnu.pixit.common.GameState;

public class GUIController {
	
	@FXML 
	private ImageView img1, img2, img3, img4, img5, img6, img7;	
	
	private ImageView[] imageSlots;
	private Integer[] imageId;
	
	private Map<Integer, Image> imageCache = new HashMap<>();
	
	@FXML
	private VBox czarBox;
	
	@FXML
	private VBox nameList;
	
	private Map<String, Label> nameListMap = new HashMap<>();
	
	@FXML
	private Label czarTheme;
	
	@FXML
	private TextField czarThemeTextfield;
	
	@FXML
	private Label cardsLabel;
	
	@FXML
	private Label infoBox;
	
	@FXML 
	private ImageView imgHover;
	
	@FXML
	private StackPane hoverPane;
	
	@FXML
	private StackPane hoverBorder;
	
	@FXML
	private GridPane mainGrid;
	
	@FXML
	private Label mainLabel;
	
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
					Client.instance.state = GameState.STATE_WAITING_FOR_PLAYERS; //Can't submit more than once
					highlightImage(imageSlot);
					try {
						Client.instance.proxy.playImage(imageSlot);
					} catch(IllegalArgumentException e) {
						Client.instance.state = GameState.STATE_WAITING_FOR_CARDS;
						highlightImage(-1);
					}
					break;
				case STATE_WAITING_FOR_GUESS :
					Client.instance.state = GameState.STATE_WAITING_FOR_PLAYERS; //Can't guess more than once
					highlightImage(imageSlot);
					try {
						Client.instance.proxy.playImageGuess(imageId[imageSlot]);
					} catch(IllegalArgumentException e) {
						Client.instance.state = GameState.STATE_WAITING_FOR_GUESS;
						highlightImage(-1);
					}
					break;
				case STATE_WAITING_FOR_CZAR :
				case STATE_WAITING_FOR_CZAR_YOU :
				case STATE_WAITING_FOR_GUESS_CZAR:
				case STATE_WAITING_FOR_PLAYERS:
				case STATE_GAME_OVER:
					System.out.println("Can't click Images as Czar / while Czar is choosing the Theme");
					break;
        	}
        }
	}
	
	@FXML 
	protected void handleMouseEnter(MouseEvent event) {
		Object obj = event.getSource();

        if ( obj instanceof StackPane ) //Probably an Image
        {
        	Integer imageSlot = Integer.parseInt((String) ((StackPane) obj).getUserData()) - 1;
        	
        	if(imageId[imageSlot] == -1)
        		return;
        	
        	Image image = imageSlots[imageSlot].getImage();
        	
        	imgHover.setImage(image);
        	
        	double aspectRatio = image.getWidth() / image.getHeight();
        	double realWidth = Math.min(imgHover.getFitWidth(), imgHover.getFitHeight() * aspectRatio) - 1;
        	double realHeight = Math.min(imgHover.getFitHeight(), imgHover.getFitWidth() / aspectRatio) - 1;
        	
        	hoverBorder.setMaxHeight(realHeight);
        	hoverBorder.setMaxWidth(realWidth);
        	
        	hoverPane.setVisible(true);
        }
	}
	
	@FXML 
	protected void handleMouseExit(MouseEvent event) {
		hoverPane.setVisible(false);
	}
	
	
	
	public void initialize() {
		imageSlots = new ImageView[]{img1, img2, img3, img4, img5, img6, img7};
		imageId = new Integer[]{-1, -1, -1, -1, -1, -1, -1};
		
		hoverPane.setVisible(false);
		hoverPane.setMouseTransparent(true);
		
		mainLabel.setVisible(false);
		mainLabel.setMouseTransparent(true);
	}
	
	public void showCzarBox(boolean show) {
		czarBox.setVisible(show);
		czarTheme.setVisible(!show);
	}
	
	public void setCzarTheme(String theme) {
		czarTheme.setText(theme);
	}
	
	public void setInfoBox(String info) {
		infoBox.setText(info);
	}
	
	@Deprecated
	public void setPoints(Integer points) {
		//pointsLabel.setText("Points: " + points);
	}
	
	public void setDeckStats(Integer current, Integer max) {
		cardsLabel.setText("Cards in Deck: " + current + " / " + max);
	}
	
	public void setMainText(String text) {
		boolean isBlank = text.isBlank();
		
		mainLabel.setText(text);
		mainLabel.setVisible(!isBlank);
			
		mainGrid.setVisible(isBlank);
		mainGrid.setMouseTransparent(!isBlank);
	}
	
	public void setPlayersPoints(Map<String, Integer> points) {
		for(Entry<String, Integer> player : points.entrySet()) {
			Label playerLabel = nameListMap.get(player.getKey());
			
			if (playerLabel == null) {
				playerLabel = new Label();
				nameListMap.put(player.getKey(), playerLabel);
				nameList.getChildren().add(playerLabel);
			}
			
			playerLabel.setText(player.getKey() + ": " + player.getValue());
		}
	}
	
	public void highlightPlayer(String player) {
		for(Entry<String, Label> playerLabel : nameListMap.entrySet()) {

			if (playerLabel.getKey().equals(player)) 
				playerLabel.getValue().setStyle("-fx-font-weight: bold;");
			else
				playerLabel.getValue().setStyle("");
			
		}
	}
	
	public void highlightImage(int imageSlot) {
		for(int i = 0; i < 7; i++) {
			if(imageSlot == -1) {
				if(imageId[i] == -1)
					continue;
				
				imageSlots[i].setOpacity(1);
				continue;
			}
			
			if(imageId[i] == -1)
				continue;
			
			if(i != imageSlot)
				imageSlots[i].setOpacity(0.25);
			
			else
				imageSlots[i].setOpacity(1);
		}
	}
	
	public void highlightImageById(int image) {
		for(int i = 0; i < 7; i++) {
			if(imageId[i] == image)
				imageSlots[i].setOpacity(1);
			
			else if(imageId[i] != -1)
				imageSlots[i].setOpacity(0.25);
		}
	}
	
	private Image loadImage(PiXitImageRequest imageReq) {
		Image image = imageCache.get(imageReq.id);
		if(image == null) {
			//Img was not cached in RAM
			image = Client.instance.loadFromCache(imageReq.hash);
			
			if(image == null) {
				//Img was not cached on HDD
				image = Client.instance.proxy.loadImage(imageReq);
				imageCache.put(imageReq.id, image);
			}
		}
		return image;
	}
	
	public void setNewImages(List<PiXitImageRequest> imageId) {
		for(int i = 0; i < 7; i++) {
			if(imageId.size() <= i || imageId.get(i) == null) {
				//No Img
				imageSlots[i].setVisible(false);
				this.imageId[i] = -1;
			}
			else if(imageId.get(i).id != this.imageId[i]) {
				//New Img
				imageSlots[i].setVisible(true);
				imageSlots[i].setOpacity(1);
				
				this.imageId[i] = imageId.get(i).id;
				Image image = loadImage(imageId.get(i));
				
				imageSlots[i].setImage(image);
			}
			else {
				imageSlots[i].setOpacity(1);
				//Keep Img
			}
		}
	}
	
	public void setNewImagesCleanup(Map<Integer, PiXitImageRequest> imageId) {
		for(int i = 0; i < 7; i++) {
			if(imageId.size() <= i || imageId.get(i) == null) {
				//No Img				
				imageSlots[i].setVisible(false);
				imageSlots[i].setImage(null);
				
				imageCache.remove(this.imageId[i]);
				this.imageId[i] = -1;
				
			}
			else if(imageId.get(i).id != this.imageId[i]) {
				//New Img
				
				imageSlots[i].setVisible(true);
				imageSlots[i].setOpacity(1);
				
				Image image = loadImage(imageId.get(i));
				imageSlots[i].setImage(image);
				
				imageCache.remove(this.imageId[i]);
				
				this.imageId[i] = imageId.get(i).id;
			}
			else {
				imageSlots[i].setOpacity(1);
				//Keep Img
			}
		}
	}
}
