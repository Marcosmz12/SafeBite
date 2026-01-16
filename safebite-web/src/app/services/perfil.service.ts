import { Injectable, inject } from '@angular/core';
import { Firestore, doc, docData, setDoc, updateDoc } from '@angular/fire/firestore';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class PerfilService {
  private firestore = inject(Firestore);

  // Obtener los datos del perfil (incluyendo alergias)
  getPerfil(uid: string): Observable<any> {
    const docRef = doc(this.firestore, `perfiles/${uid}`);
    return docData(docRef);
  }

  // Guardar o actualizar las alergias
  guardarAlergias(uid: string, alergias: string[]) {
    const docRef = doc(this.firestore, `perfiles/${uid}`);
    return setDoc(docRef, { alergias }, { merge: true });
  }
}