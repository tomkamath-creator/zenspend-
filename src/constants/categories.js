import { THEME } from './theme';

export const CATEGORIES = [
  {
    id: 'groceries',
    name: 'Groceries',
    icon: 'local-grocery-store',
    iconFamily: 'MaterialIcons',
    color: THEME.colors.catGroceries,
    bgColor: '#EFF5F0',
  },
  {
    id: 'food',
    name: 'Food & Dining',
    icon: 'restaurant',
    iconFamily: 'MaterialIcons',
    color: THEME.colors.catFood,
    bgColor: '#FAF2EB',
  },
  {
    id: 'bills',
    name: 'Bills & Utilities',
    icon: 'receipt-long',
    iconFamily: 'MaterialIcons',
    color: THEME.colors.catBills,
    bgColor: '#EDF3F8',
  },
  {
    id: 'travel',
    name: 'Travel & Transport',
    icon: 'directions-car',
    iconFamily: 'MaterialIcons',
    color: THEME.colors.catTransport,
    bgColor: '#F1F2F8',
  },
  {
    id: 'shopping',
    name: 'Shopping',
    icon: 'local-mall',
    iconFamily: 'MaterialIcons',
    color: THEME.colors.catShopping,
    bgColor: '#F8EEF4',
  },
  {
    id: 'health',
    name: 'Health & Care',
    icon: 'spa',
    iconFamily: 'MaterialIcons',
    color: THEME.colors.catHealth,
    bgColor: '#ECF6F4',
  },
  {
    id: 'entertainment',
    name: 'Entertainment',
    icon: 'movie',
    iconFamily: 'MaterialIcons',
    color: THEME.colors.catEntertainment,
    bgColor: '#F3F0F8',
  },
  {
    id: 'education',
    name: 'Education',
    icon: 'school',
    iconFamily: 'MaterialIcons',
    color: THEME.colors.catEducation,
    bgColor: '#EDF5F1',
  },
  {
    id: 'other',
    name: 'Other',
    icon: 'more-horiz',
    iconFamily: 'MaterialIcons',
    color: THEME.colors.catOther,
    bgColor: '#F1F4F2',
  },
];

export const getCategoryById = (id) => {
  return CATEGORIES.find((cat) => cat.id === id) || CATEGORIES[CATEGORIES.length - 1];
};
