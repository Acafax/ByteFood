package org.example.posFX.apiCommunication;

import org.example.posFX.objects.ModificationTemplate;
import org.example.posFX.objects.MenuItem;

import java.util.List;

public record MenuResponseDTO (List<MenuItem> menuItems, List<ModificationTemplate> modificationTemplates) {
}
