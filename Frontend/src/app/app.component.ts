import { Component, OnInit, HostListener } from '@angular/core';
import { ApiService } from './utils/services/api.service';
import Swal from 'sweetalert2';
import { Router } from '@angular/router';
import { Idle, DEFAULT_INTERRUPTSOURCES } from '@ng-idle/core';
import { DataService } from './utils/services/data.service';
import { ClarityIcons } from '@clr/icons';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})

export class AppComponent implements OnInit {

  title = 'Dashboard';
  timedOut: boolean = false;

  constructor(private api: ApiService,
    private router: Router,
    private idle: Idle,
    private dataService: DataService) {
  }


  @HostListener('window:beforeunload', ['$event'])
  logoutBeforeUnloading($event) {
    $event.returnValue = "Bitte loggen Sie sich vorher aus";
  }

  @HostListener('window:onunload', ['$event'])
  logout(){
    console.log(true)
    this.api.logout();
  }

  ngOnInit(): void {
    // the user receives a first warning after either 10 minutes or a custom value (can be changed in the settings)
    this.dataService.idleCounter.subscribe(
      idle => this.idle.setIdle(parseInt(idle) * 60 || 600)
    );

    // the user is logged out after either 2 minutes or a custom value (can be changed in the settings)
    this.dataService.timeoutCounter.subscribe(
      timeout => this.idle.setTimeout(parseInt(timeout) * 60 || 120)
    );

    // countdown will be interrupted through mousehover, keyboard etc
    this.idle.setInterrupts(DEFAULT_INTERRUPTSOURCES);

    this.idle.onIdleEnd.subscribe(() => {
      console.log('No longer idle.');
      this.reset();
    });

    this.idle.onTimeout.subscribe(() => {
      this.timedOut = true;
      console.log('Timed out!');
      this.api.logout();
      Swal.fire(
        "Inaktivität",
        "Aufgrund von Inaktivität wurden Sie automatisch ausgeloggt. Sie werden nun zur Login-Seite weitergeleitet",
        "info"
      ).then(() => setTimeout(window.location.reload.bind(window.location), 3000));
      this.router.navigate(['/login']);
    });

    this.idle.onIdleStart.subscribe(() => {
      console.log('You\'ve gone idle!');
      Swal.fire(
        "Inaktivität",
        "Aufgrund von Inaktivität werden Sie in Kürze automatisch ausgeloggt",
        "info"
      ).then(() => this.reset()) //if the user is online again
    });

    this.idle.onTimeoutWarning.subscribe(countdown => console.log('You will time out in ' + countdown + ' seconds!'));
    this.reset();
  }

  private reset() {
    this.idle.watch();
    this.timedOut = false;
  }
}
