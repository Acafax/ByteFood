package org.example.builders;

import java.util.Map;

public class OnboardingTestBuilder {

    public static RestaurantBuilder restaurant() {
        return new RestaurantBuilder();
    }

    public static EmployeeBuilder employee() {
        return new EmployeeBuilder();
    }

    public static class RestaurantBuilder {

        private String restaurantName = "Test Restaurant";
        private String stockName = "Main Warehouse";

        public RestaurantBuilder withRestaurantName(String restaurantName) {
            this.restaurantName = restaurantName;
            return this;
        }

        public RestaurantBuilder withStockName(String stockName) {
            this.stockName = stockName;
            return this;
        }

        public Map<String, Object> buildMap() {
            return Map.of(
                    "restaurantName", restaurantName,
                    "stockName", stockName
            );
        }
    }

    public static class EmployeeBuilder {

        private String email = "newemployee@test.com";
        private String password = "securepassword1";
        private String name = "Jan";
        private String lastName = "Kowalski";

        public EmployeeBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public EmployeeBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public EmployeeBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public EmployeeBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Map<String, Object> buildMap() {
            return Map.of(
                    "email", email,
                    "password", password,
                    "name", name,
                    "lastName", lastName
            );
        }
    }
}
