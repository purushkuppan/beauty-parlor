export type ServiceCategory = 'HAIR' | 'SKIN' | 'NAILS' | 'MAKEUP';

export interface BeautyService {
  id: string;
  name: string;
  description: string;
  category: ServiceCategory;
  price: number;
  durationMins: number;
}
