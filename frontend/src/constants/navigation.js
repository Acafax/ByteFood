import { LayoutDashboard, Package } from 'lucide-react';

export const MENU_ITEMS = [
  { path: '/dashboard', icon: LayoutDashboard, label: 'Panel główny' },
  { path: '/stock', icon: Package, label: 'Magazyn' },
  { path: '/create-semi-product', icon: null, label: 'Tworzenie półproduktu' },
  { path: '/create-modification', icon: null, label: 'Tworzenie modyfikacji' },
  { path: '/create-product', icon: null, label: 'Tworzenie produktu' },
  { path: '/create-set', icon: null, label: 'Tworzenie zestawu' },
];
