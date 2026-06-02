package org.example.posFX;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.posFX.apiCommunication.ApiClient;
import org.example.posFX.apiCommunication.MenuApiService;
import org.example.posFX.apiCommunication.MenuResponseDTO;
import org.example.posFX.auth.device.CredentialStorageException;
import org.example.posFX.auth.device.DeviceAuthService;
import org.example.posFX.navigation.SceneNavigator;
import org.example.posFX.session.AuthSession;
import org.example.posFX.objects.MenuItem;
import org.example.posFX.objects.ModificationTemplate;
import org.example.posFX.objects.OrderItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.util.*;

public class ProductController {

    private final AuthSession authSession;
    private final SceneNavigator sceneNavigator;
    private final MenuApiService menuApiService;
    private final ApiClient apiClient;
    private final ObjectMapper objectMapper;
    private final DeviceAuthService deviceAuthService;


    public ProductController(
            AuthSession authSession,
            SceneNavigator sceneNavigator,
            ObjectMapper objectMapper,
            ApiClient apiClient,
            MenuApiService menuApiService,
            DeviceAuthService deviceAuthService
    ) {
        this.authSession = authSession;
        this.sceneNavigator = sceneNavigator;
        this.menuApiService = menuApiService;
        this.apiClient = apiClient;
        this.objectMapper = objectMapper;
        this.deviceAuthService = deviceAuthService;
    }

    @FXML private BorderPane rootPane;
    @FXML private GridPane mainContentPane;
    @FXML private ListView<OrderItem> orderListView;
    @FXML private Label totalPriceLabel;
    @FXML private TabPane mainCategoryTabs;

    private final ObservableList<OrderItem> orderItems = FXCollections.observableArrayList();
    private final List<MenuItem> menuItems = new ArrayList<>();
    public static HashMap<String, ModificationTemplate> possibleModificationMap = new HashMap<>();

    @FXML
    public void initialize() {
        initializeMenuItems();
        orderListView.setItems(orderItems);
        createCategoryTabs();
        configureOrderListViewCellFactory();
    }

    @FXML
    private void onClearOrderClicked() {
        clearOrder();
    }

    @FXML
    private void onPlaceOrderClicked() {
        if (orderItems.isEmpty()) {
            showOrderMessage(Alert.AlertType.WARNING, "Puste zamówienie", "Dodaj produkty do koszyka.");
            return;
        }

        setBusy(true);
        List<OrderItem> itemsSnapshot = List.copyOf(orderItems);
        menuApiService.submitOrderAsync(
                itemsSnapshot,
                message -> Platform.runLater(() -> {
                    setBusy(false);
                    clearOrder();
                    showOrderMessage(Alert.AlertType.INFORMATION, "Zamówienie", message);
                }),
                message -> Platform.runLater(() -> {
                    setBusy(false);
                    showOrderMessage(Alert.AlertType.ERROR, "Błąd zamówienia", message);
                })
        );
    }

    private void showOrderMessage(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }

    @FXML
    private void onSessionMenuClicked() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Sesja");
        dialog.initModality(Modality.APPLICATION_MODAL);

        Label description = new Label("Wybierz operację:");
        description.setWrapText(true);
        description.setMaxWidth(360);
        description.getStyleClass().add("session-menu-description");

        VBox content = new VBox(12, description);
        content.setMinWidth(360);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm()
        );
        dialog.getDialogPane().getStyleClass().add("session-menu-dialog");

        ButtonType logoutType = new ButtonType("Wyloguj", ButtonBar.ButtonData.OK_DONE);
        ButtonType resetType = new ButtonType("Reset ApiKey urządzenia", ButtonBar.ButtonData.OTHER);
        ButtonType cancelType = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(logoutType, resetType, cancelType);

        dialog.showAndWait().ifPresent(result -> {
            if (result == logoutType) {
                onLogoutClicked();
            } else if (result == resetType) {
                onResetDeviceApiKeyClicked();
            }
        });
    }

    private void onLogoutClicked() {
        authSession.clear();
        sceneNavigator.showLogin();
    }

    private void onResetDeviceApiKeyClicked() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Reset ApiKey urządzenia");
        confirmation.setHeaderText("Czy na pewno chcesz zresetować ApiKey?");
        confirmation.setContentText(
                "Klucz zostanie usunięty z serwera i z magazynu systemowego. "
                        + "Aby kontynuować pracę, trzeba będzie ponownie zarejestrować urządzenie."
        );
        confirmation.initModality(Modality.APPLICATION_MODAL);

        Optional<ButtonType> decision = confirmation.showAndWait();
        if (decision.isEmpty() || decision.get() != ButtonType.OK) {
            return;
        }

        setBusy(true);
        deviceAuthService.resetDeviceApiKeyAsync().whenComplete((ignored, throwable) -> Platform.runLater(() -> {
            setBusy(false);

            if (throwable != null) {
                showResetError(resolveResetErrorMessage(throwable));
                return;
            }

            sceneNavigator.showDeviceAuthentifier();
        }));
    }


    private void showResetError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Reset ApiKey urządzenia");
        alert.setHeaderText("Reset nie powiódł się");
        alert.setContentText(message);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }

    private String resolveResetErrorMessage(Throwable throwable) {
        Throwable cause = throwable.getCause() != null ? throwable.getCause() : throwable;
        if (cause instanceof CredentialStorageException credentialStorageException) {
            return credentialStorageException.getMessage();
        }
        return "Nie udało się zresetować ApiKey urządzenia. Spróbuj ponownie.";
    }

    private void initializeMenuItems() {
        try (InputStream mockData = getClass().getResourceAsStream("/mockDataFromDB.json")){

            ObjectMapper objectMapper = new ObjectMapper();
            MenuResponseDTO menuResponse = objectMapper.readValue(mockData, MenuResponseDTO.class);

            menuItems.addAll(menuResponse.menuItems());

            menuResponse.modificationTemplates().forEach(modificationTemplate -> {
                possibleModificationMap.put(modificationTemplate.name(), modificationTemplate);
            });
        } catch (IOException e) {
            throw new RuntimeException("Nie można wczytać menu z mockDataFromDB.json", e);
        }
    }

    private void returnToMenuView() {
        Tab selectedTab = mainCategoryTabs.getSelectionModel().getSelectedItem();
        if (selectedTab!=null){
            showMenuOfSelectedCategory(selectedTab.getText());
        }else {
            mainCategoryTabs.getSelectionModel().selectFirst();
        }
    }

    private void createCategoryTabs(){
        List<String> categories = menuItems.stream()
                .map(MenuItem::getCategory)
                .distinct()
                .toList();

        for (String category : categories){
            Tab categoryTab = new Tab();
            categoryTab.setText(category);
            categoryTab.setClosable(false);
            categoryTab.setOnSelectionChanged(event -> {
                if (categoryTab.isSelected()){
                    showMenuOfSelectedCategory(category);
                }
            });
            mainCategoryTabs.getTabs().add(categoryTab);
        }
        if (!mainCategoryTabs.getTabs().isEmpty()) {
            mainCategoryTabs.getSelectionModel().selectFirst();
        }
    }

    private void showMenuOfSelectedCategory(String category) {
        mainContentPane.getChildren().clear();
        List<MenuItem> categoryItems = menuItems.stream()
                .filter(item -> item.getCategory().equals(category))
                .toList();

        int column = 0;
        int row = 0;
        for (MenuItem item : categoryItems) {
            try {
                Node itemCardNode = createMenuItemCard(item);
                mainContentPane.add(itemCardNode, column, row);

                column++;
                if (column >= 3) {
                    column = 0;
                    row++;
                }
            } catch (IOException e) {
                throw new UncheckedIOException("Błąd podczas ładowania karty produktu: " + item.getName(), e);
            }
        }
    }

    private Node createMenuItemCard(MenuItem item) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-item-card.fxml"));
        Node cardNode = loader.load();

        MenuItemCardController cardController = loader.getController();
        cardController.setData(item, this::addToOrder);

        return cardNode;
    }

    private void configureOrderListViewCellFactory() {
        orderListView.setCellFactory(listView -> new ListCell<>() {
            private final FXMLLoader loader = new FXMLLoader(getClass().getResource("order-item-cell.fxml"));
            private OrderItemCellController controller;
            private Node cellGraphic;

            {
                try {
                    cellGraphic = loader.load();
                    controller = loader.getController();
                } catch (IOException e) {
                    throw new RuntimeException("Nie można załadować order-item-cell.fxml", e);
                }
            }

            @Override
            protected void updateItem(OrderItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    controller.setData(
                            item,
                            orderItemToRemove -> removeFromOrder(orderItemToRemove),
                            orderItemToModify -> showModifyMenu(orderItemToModify)
                    );
                    setGraphic(cellGraphic);
                }
            }
        });
    }

    private void showModifyMenu(OrderItem orderItem) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("edit-item-view.fxml"));
            Parent editView = loader.load();

            EditPanelController editController = loader.getController();
            editController.initData(
                    orderItem,
                    editedItem -> onSaveAction(orderItem,editedItem),
                    this::onCancelAction
            );

            Scene scene = new Scene(editView, 750, 650);

            try {
                String css = getClass().getResource("style.css").toExternalForm();
                scene.getStylesheets().add(css);
            } catch (Exception ex) {
                System.err.println("Nie można załadować pliku CSS: " + ex.getMessage());
            }

            Stage editStage = new Stage();
            editStage.setTitle("Edycja produktu");
            editStage.setScene(scene);
            editStage.setMinWidth(750);
            editStage.setMinHeight(650);
            editStage.initModality(Modality.APPLICATION_MODAL);
            editStage.show();
        } catch (IOException e) {
            throw new UncheckedIOException("Nie można otworzyć panelu edycji produktu", e);
        }
    }

    private void onSaveAction(OrderItem originalItem,OrderItem editedItem) {
        handleEditedItem(originalItem,editedItem);
    }

    private void onCancelAction() {
        // no-op: panel closed without saving
    }

    private void handleEditedItem(OrderItem originalItem, OrderItem editedItem) {
        if (originalItem.getQuantity() <= 1) {
            orderItems.remove(originalItem);
        }

        isUniqueItemInTheOrderList(editedItem);

        orderListView.refresh();
        updateTotalPrice();
    }

    private void isUniqueItemInTheOrderList(OrderItem editedItem){
        OrderItem existingItemFromOrder = orderItems.stream()
                .filter(item -> Objects.equals(item.getMenuItem().getName(), editedItem.getMenuItem().getName())
                        && Objects.equals(item.getModify(), editedItem.getModify()))
                .findFirst()
                .orElse(null);
        if (existingItemFromOrder == null){
            orderItems.add(editedItem);
        }else {
            existingItemFromOrder.setQuantity(existingItemFromOrder.getQuantity()+1);
        }
    }

    private void addToOrder(MenuItem menuItem) {
        OrderItem existingItem = orderItems.stream()
                .filter(item -> item.getMenuItem().getName().equals(menuItem.getName()))
                .filter(item -> item.getModify()==null)
                .findFirst()
                .orElse(null);
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + 1);
            orderListView.refresh();
        } else {
            orderItems.add(new OrderItem(menuItem, 1));
        }
        updateTotalPrice();
    }

    private void removeFromOrder(OrderItem orderItem) {
        if (orderItem.getQuantity()>1){
            int newQuantity = orderItem.getQuantity()-1;
            orderItem.setQuantity(newQuantity);
            orderListView.refresh();
            updateTotalPrice();
        }else {
            orderItems.remove(orderItem);
        updateTotalPrice();
        }
    }

    private void clearOrder() {
        orderItems.clear();
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        BigDecimal total = orderItems.stream()
                .map(orderItem -> orderItem.priceOfItemWithMods(orderItem))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        double totalDouble = total.doubleValue();
        totalPriceLabel.setText(String.format("%.2f zł", totalDouble));
    }



    private void setBusy(boolean busy) {
        if (rootPane != null) {
            rootPane.setDisable(busy);
            return;
        }
        if (mainContentPane != null) {
            mainContentPane.setDisable(busy);
        }
        if (orderListView != null) {
            orderListView.setDisable(busy);
        }
        if (totalPriceLabel != null) {
            totalPriceLabel.setDisable(busy);
        }
        if (mainCategoryTabs != null) {
            mainCategoryTabs.setDisable(busy);
        }
    }
}
