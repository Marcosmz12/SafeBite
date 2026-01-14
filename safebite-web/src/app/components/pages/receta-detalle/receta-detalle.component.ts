import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Observable } from 'rxjs';
import { AsyncPipe, CommonModule } from '@angular/common';
import { RecetasService } from '../../../services/recetas.service';
import { Receta } from '../../../models/receta';

@Component({
  selector: 'app-receta-detalle',
  standalone: true,
  imports: [CommonModule, AsyncPipe, RouterLink],
  templateUrl: './receta-detalle.component.html',
  styleUrl: './receta-detalle.component.css'
})
export class RecetaDetalleComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private recetasService = inject(RecetasService);
  
  receta$!: Observable<Receta>;

  ngOnInit() {
    // Obtenemos el ID de la URL
    const id = this.route.snapshot.paramMap.get('id');
    
    if (id) {
      // Llamamos al servicio
      this.receta$ = this.recetasService.getRecetaById(id);
    }
  }
}