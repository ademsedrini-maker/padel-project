import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginMembre } from './login-membre';

describe('LoginMembre', () => {
  let component: LoginMembre;
  let fixture: ComponentFixture<LoginMembre>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginMembre],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginMembre);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
