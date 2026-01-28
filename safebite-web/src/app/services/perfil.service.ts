import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class PerfilService {
  private http = inject(HttpClient);
  private apiUrl = 'https://safebite-7dgr.onrender.com/api/perfil'; // Tu servidor Node

  getPerfil(uid: string): Observable<any> {
    // Asegúrate de que pones la / entre la URL y el UID
    return this.http.get(`${this.apiUrl}/${uid}`);
  }

  guardarAlergias(uid: string, alergias: string[]): Observable<any> {
    // Mandamos un POST a Node.js con las nuevas alergias
    return this.http.post(`${this.apiUrl}/${uid}`, { alergias });
  }
}