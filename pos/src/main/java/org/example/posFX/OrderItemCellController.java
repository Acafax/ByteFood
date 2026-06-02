package org.example.posFX;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.example.posFX.objects.ModificationTemplate;
import org.example.posFX.objects.OrderItem;
import javafx.scene.control.Label;
import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.example.posFX.ProductController.*;

public class OrderItemCellController {

    @FXML private VBox itemContainer;
    @FXML private Button modifyButton;
    @FXML private Button removeButton;

    private OrderItem currentItem;
    private Consumer<OrderItem> removeAction;
    private Consumer<OrderItem> modifyAction;



    public void setData(OrderItem item, Consumer<OrderItem> onRemove, Consumer<OrderItem> onModify) {
        this.currentItem = item;
        this.removeAction = onRemove;
        this.modifyAction = onModify;

        itemContainer.getChildren().clear();


        if (item.getMenuItem().getType().equals(ItemType.COMBO)){
            VBox comboHeader = new VBox();
            Label nameLabel = createLabel(item.getMenuItem().getName(), "item-name");
            Label detailsLabel = createLabel(
                String.format("Ilość: %d | cena: %f", item.getQuantity(), item.getMenuItem().getPrice())
                , "item-details");
            comboHeader.getChildren().addAll(nameLabel,detailsLabel);
            itemContainer.getChildren().add(comboHeader);

            item.getMenuItem().getComponents().forEach(component -> {
                VBox componentsBox = new VBox();
                Label componentNameLabel = createLabel(component.menuItem().getName(), "item-name");
                Label componentDetailsLabel = createLabel(
                        String.format("Ilość: %d | cena: %f", component.quantity(), component.menuItem().getPrice())
                        , "item-details");

                Label modificationLabel = updateModificationsView();

                componentsBox.getChildren().addAll(componentNameLabel, componentDetailsLabel,modificationLabel);
                itemContainer.getChildren().add(componentsBox);
            });
            updateModificationsView();
        }else {
            VBox selectedProductBox = new VBox();


            Label nameLabel = createLabel(item.getMenuItem().getName(), "item-name");
            Label detailsLabel = updateDetails(item);
            Label modificationLabel = updateModificationsView();

            itemContainer.getChildren().addAll(nameLabel,detailsLabel,modificationLabel);
        }
    }

    private Label createLabel(String labelName, String styleClass){
        Label label = new Label(labelName);
        label.getStyleClass().add(styleClass);
        return label;
    }

    private Label updateModificationsView(){
        Map<String, Integer> mods = currentItem.getModify();
        Label modificationsLabel = new Label();
        if (mods != null && !mods.isEmpty()){
            String modifications = mods.entrySet().stream()
                    .map(mod ->{
                        ModificationTemplate modificationTemplate = possibleModificationMap.get(mod.getKey());
                        BigDecimal totalPrice = modificationTemplate.price().multiply(new BigDecimal(mod.getValue()));
                        String info = modificationTemplate.price() + " x " + mod.getValue() + " = " + modificationTemplate.price().multiply(new BigDecimal(mod.getValue()));
                        return String.format("%s : %.2f x %d = %.2f zł",
                                modificationTemplate.name(),
                                modificationTemplate.price(),
                                mod.getValue(),
                                totalPrice);
                    }).collect(Collectors.joining("\n"));

            modificationsLabel.setText(modifications);
            modificationsLabel.setVisible(true);
            modificationsLabel.setManaged(true);
        }else {
            modificationsLabel.setText("");
            modificationsLabel.setVisible(false);
            modificationsLabel.setManaged(false);
        }
        return modificationsLabel;
    }


    /**
     * Method to update the details label.
     * Needed when the quantity changes without reloading the entire cell.
     */
    public Label updateDetails(OrderItem orderItem) {
        Label updatedDetails = new Label(String.format("Ilość: %d | %.2f zł",
                currentItem.getQuantity(),
                currentItem.priceOfItemWithMods(orderItem)
        ));
        updatedDetails.getStyleClass().add("item-details");
        return updatedDetails;
    }


    @FXML
    private void onRemoveClicked() {
        if (removeAction != null) {
            removeAction.accept(currentItem);
        }
    }

    @FXML
    private void onModifyClicked() {
        if (modifyAction != null) {
            modifyAction.accept(currentItem);
        }
    }


}
