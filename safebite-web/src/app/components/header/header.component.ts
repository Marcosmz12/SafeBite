import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router'; // <--- IMPORTANTE

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterLink, RouterLinkActive], // <--- DEBE ESTAR AQUÍ
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {}