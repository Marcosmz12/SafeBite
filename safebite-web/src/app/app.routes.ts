import { Routes } from '@angular/router';
import { HeroComponent } from './components/pages/hero/hero.component';
import { RecetasComponent } from './components/pages/recetas/recetas.component';
import { RecetaDetalleComponent } from './components/pages/receta-detalle/receta-detalle.component';


export const routes: Routes = [
  { path: '', component: HeroComponent }, // El logo te llevará aquí
  { path: 'recetas', component: RecetasComponent },
  { path: 'receta/:id', component: RecetaDetalleComponent },
  // ... otras rutas
];