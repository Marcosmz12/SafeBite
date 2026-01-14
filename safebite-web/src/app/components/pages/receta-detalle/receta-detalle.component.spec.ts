import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecetaDetalleComponent } from './receta-detalle.component';

describe('RecetaDetalleComponent', () => {
  let component: RecetaDetalleComponent;
  let fixture: ComponentFixture<RecetaDetalleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecetaDetalleComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RecetaDetalleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
