package org.example.posFX;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.example.posFX.objects.ModificationTemplate;
import org.example.posFX.objects.OrderItem;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.example.posFX.ProductController.possibleModificationMap;

public class EditPanelController {

    @FXML private Label productNameLabel;
    @FXML private Label infoLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private FlowPane modificationPanel;

    private OrderItem originalItem;
    private LinkedHashMap<String, Integer> modifications = new LinkedHashMap<>();

    private Consumer<OrderItem> onSaveCallback;

    private Runnable onCancelCallback;

    public ModificationTemplate getModifyByName(String name){
        return possibleModificationMap.get(name);
    }

    public List<ModificationTemplate> getModificationTemplateListByCategory(String category){
        return possibleModificationMap.values().stream()
                .filter(modificationTemplate -> modificationTemplate.category().equals(category))
                .toList();
    }


    public void initData(OrderItem itemToEdit, Consumer<OrderItem> onSave, Runnable onCancel) {
        this.originalItem = itemToEdit;
        this.onSaveCallback = onSave;
        this.onCancelCallback = onCancel;

        // Kopiujemy istniejące modyfikacje, aby można je było edytować
        if (originalItem.getModify()!=null  ){
            this.modifications.putAll(itemToEdit.getModify());
        }
        setModificationPanel();
        productNameLabel.setText(originalItem.getMenuItem().getName());
        updateInfoLabel();
    }

    private void setModificationPanel(){
        List<ModificationTemplate> modificationTemplateList = getModificationTemplateListByCategory(originalItem.getMenuItem().getCategory());
        int colorIndex = 0;
        String[] colorVariants = {"btn-addon-variant-1", "btn-addon-variant-2", "btn-addon-variant-3", "btn-addon-variant-4"};

        for (ModificationTemplate modificationTemplate:modificationTemplateList){
            Button button = new Button();

            button.setText(modificationTemplate.name());
            button.setMnemonicParsing(false);

            button.getStyleClass().add("btn-addon");
            button.getStyleClass().add(colorVariants[colorIndex % colorVariants.length]);
            colorIndex++;

            button.setOnAction(event ->{
                modifications.merge(modificationTemplate.name(), 1, Integer::sum);
                updateInfoLabel();
            });
            modificationPanel.getChildren().add(button);

        }
    }

    @FXML
    private void onSaveClicked() {
        if(originalItem.getQuantity()>1) {
            originalItem.setQuantity(originalItem.getQuantity() - 1);
        }

        OrderItem editedItem = new OrderItem(originalItem.getMenuItem(), 1,modifications);

        if (onSaveCallback != null) {
            onSaveCallback.accept(editedItem);
        }
        closePanel();

    }

    private void addModifications(OrderItem orderItem, LinkedHashMap<String, Integer> modifications){
        LinkedHashMap<String, Integer> modify = orderItem.getModify();
        modify.putAll(modifications);
    }

    private void updateInfoLabel(){
        if (modifications.isEmpty()){
            infoLabel.setText("Brak modyfikacji");
        }else {
            String collect = modifications.entrySet().stream()
                    .map(entry -> entry.getKey() + " x " + entry.getValue())
                    .collect(Collectors.joining(" | "));
            infoLabel.setText(collect);
        }
    }

    @FXML
    private void onCancelClicked() {
        if (onCancelCallback != null) {
            onCancelCallback.run();
        }
        closePanel();
    }

    private void closePanel(){
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
