import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BuscadorSuperComponent } from './buscador-super.component';

describe('BuscadorSuperComponent', () => {
  let component: BuscadorSuperComponent;
  let fixture: ComponentFixture<BuscadorSuperComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BuscadorSuperComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BuscadorSuperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
