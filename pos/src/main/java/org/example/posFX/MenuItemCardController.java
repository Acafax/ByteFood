package org.example.posFX;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.example.posFX.objects.MenuItem;

import java.util.function.Consumer;

public class MenuItemCardController {

    @FXML private VBox card;
    @FXML private ImageView productImage;
    @FXML private Label nameLabel;
    @FXML private Label priceLabel;
    @FXML private Button addButton;

    private MenuItem currentItem;
    private Consumer<MenuItem> addAction;

    /**
     * Ustawia dane dla tej karty oraz akcję, która ma być wykonana po kliknięciu "Dodaj".
     * @param item Obiekt MenuItem do wyświetlenia.
     * @param onAdd Akcja do wykonania (zazwyczaj ProductController::addToOrder).
     */
    public void setData(MenuItem item, Consumer<MenuItem> onAdd) {
        this.currentItem = item;
        this.addAction = onAdd;

        nameLabel.setText(item.getName());
        priceLabel.setText(String.format("%.2f zł", item.getPrice()));

        loadProductImage(item.getCategory(), item.getName());
    }



    @FXML
    private void onAddButtonClicked() {
        if (addAction != null) {
            addAction.accept(currentItem);
        }
    }

    private void loadProductImage(String category, String productName) {
        try {
            String imagePath = "/images/" + category.toLowerCase() + "/" +
                             productName.toLowerCase().replace(" ", "_") + ".jpg";
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            productImage.setImage(image);
        } catch (Exception e) {
            loadDefaultImage(category);
        }
    }


    private void loadDefaultImage(String category) {
        try {
            String defaultImagePath = "/images/" + category.toLowerCase() + "/default.jpg";
            Image image = new Image(getClass().getResourceAsStream(defaultImagePath));
            productImage.setImage(image);
        } catch (Exception e) {
            // Jeśli nie ma nawet domyślnego zdjęcia, zostaw puste (szare tło z CSS)
            System.err.println("Brak zdjęcia dla kategorii: " + category);
        }
    }
}
