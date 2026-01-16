import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AlergiasManagerComponent } from './alergias-manager.component';

describe('AlergiasManagerComponent', () => {
  let component: AlergiasManagerComponent;
  let fixture: ComponentFixture<AlergiasManagerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlergiasManagerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AlergiasManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
