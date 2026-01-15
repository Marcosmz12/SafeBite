import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, AsyncPipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { RecetasService } from '../../services/recetas.service';
import { Observable } from 'rxjs';
import { Receta } from '../../models/receta';

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
    const id = this.route.snapshot.paramMap.get('id');
    if (id) this.receta$ = this.recetasService.getRecetaById(id);
  }

  imgError(event: any) {
    event.target.style.display = 'none';
  }
}