import { LayoutDashboard, Package, Users } from 'lucide-react';

export const MENU_ITEMS = [
  { path: '/dashboard', icon: LayoutDashboard, label: 'Panel główny' },
  { path: '/stock', icon: Package, label: 'Magazyn' },
  { path: '/staff', icon: Users, label: 'Personel', managerOnly: true },
  { path: '/create-semi-product', icon: null, label: 'Tworzenie półproduktu' },
  { path: '/create-modification', icon: null, label: 'Tworzenie modyfikacji' },
  { path: '/create-product', icon: null, label: 'Tworzenie produktu' },
  { path: '/create-combo', icon: null, label: 'Tworzenie zestawu' },
];
