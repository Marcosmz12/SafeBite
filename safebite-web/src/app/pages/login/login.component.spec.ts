import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { provideRouter } from '@angular/router';
import { AuthService } from '../../services/auth.service';

// Creamos un "simulador" del servicio de Auth para que el test no intente conectar a Firebase de verdad
const authServiceMock = {
  login: jasmine.createSpy('login').and.returnValue(Promise.resolve())
};

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        provideRouter([]), // Simula las rutas
        { provide: AuthService, useValue: authServiceMock } // Simula el servicio de Auth
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});