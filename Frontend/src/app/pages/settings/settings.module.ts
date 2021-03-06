import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClarityModule } from '@clr/angular';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SelectComponent } from './select/select.component';
import { SettingsComponent } from './settings.component';
import { RouterModule } from '@angular/router';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AccountComponent } from './account/account.component';


@NgModule({
  declarations: [
    SettingsComponent,
    SelectComponent,
    AccountComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    ClarityModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
  ],
  exports: [
    SettingsComponent
  ]
})

export class SettingsModule { }
