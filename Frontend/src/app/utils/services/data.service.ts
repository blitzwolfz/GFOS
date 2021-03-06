import { Injectable } from '@angular/core';
import { Mitarbeiter, Arbeitsgruppe } from '../interfaces/default.model';
import { environment } from 'src/environments/environment';
import { employeeSamples, groupSamples } from '../mock.data';
import { Observable, of, Subject, BehaviorSubject, interval } from 'rxjs';

@Injectable({
  providedIn: 'root'
})

/**
 * This service explicity <b>doesn't</b> work with the API
 * it only serves static data that has been a result of previous API requests to the User
 * 
 * @author Jonathan
 */
export class DataService {

  private currentUser: Mitarbeiter;
  private auth: string;
  private groups: Arbeitsgruppe[];
  public userSetHours: boolean = false;

  //initialize subjects using the already stored values
  public idleCounter: BehaviorSubject<string> = new BehaviorSubject<string>(this.getIdle());
  public timeoutCounter: BehaviorSubject<string> = new BehaviorSubject<string>(this.getTimeout());
  private _mobile: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(this.isMobileDevice());
  public userSubject: Subject<Mitarbeiter> = new Subject<Mitarbeiter>();
  public groupSubject: Subject<Array<Arbeitsgruppe>> = new Subject<Arbeitsgruppe[]>();

  constructor() {
    /*
     * every second the user agent is checked again, this allows live refreshment
     * when the user adjusts the screen size, toggles mobile/desktop view, etc.
     */
    interval(1000).subscribe(() => this._mobile.next(this.isMobileDevice()));
  }

  /*
   * basic getters and setters for methods
   * 
   * Note: some members will subscribe to the counters
   * since they can provide live updates
   * 
   * the methods below could've been implemented using js get/set syntax
   * but this looks cleaner in my opinion
   */

  /**
   * @param user the current logged in user as object
   */
  public setUser(user: Mitarbeiter) {
    this.userSubject.next(user);
    this.currentUser = user;
  }

  /**
   * @param asObservable whether the object should be returned as object or Observable
   * @returns the current logged in user as Object or Observable
   */
  public getUser(asObservable: boolean): Observable<Mitarbeiter>;
  public getUser(): Mitarbeiter;
  public getUser(asObservable?: boolean): Mitarbeiter | Observable<Mitarbeiter> {
    if (asObservable) {
      if (!environment.production) return of(employeeSamples[0]);
      return this.userSubject.asObservable();
    }
    if (!environment.production) return employeeSamples[0];
    return this.currentUser;
  }

  /**
   * @param g The new User groups array the Subject holds
   */
  public setGroups(g: Arbeitsgruppe[]) {
    this.groupSubject.next(g);
    this.groups = g;
  }

  /**
   * @param asObservable whether the returned value should be only an Object array
   *  or an Observable that holds the Array
   * @returns the current user groups either as Observable or as Objectarray
   */
  public getGroups(asObservable: boolean): Observable<Array<Arbeitsgruppe>>;
  public getGroups(): Array<Arbeitsgruppe>;
  public getGroups(asObservable?: boolean): Observable<Arbeitsgruppe[]> | Arbeitsgruppe[] {
    if (asObservable) {
      if (!environment.production) return of(groupSamples);
      return this.groupSubject.asObservable();
    }
    if (!environment.production) return groupSamples;
    return this.groups;
  }

  /**
   * @param auth: set the current auth token
   */
  public setAuth(auth: string): void {
    this.auth = auth;
  }

  /**
   * @returns the current auth token
   */
  public getAuth(): string {
    return this.auth;
  }

  /**
   * For further information about how/where this is used, see app.component.ts
   * @returns the current timeout countdown in minutes
   */
  public getTimeout(): string {
    if (!localStorage.getItem('timeout')) localStorage.setItem('timeout', "2");
    return localStorage.getItem("timeout");
  }

  /**
   * @param t the new timeout duration in minutes
   */
  public setTimeout(t: string) {
    //using to notify subscriber components of the value that something has changed
    this.timeoutCounter.next(t);
    localStorage.setItem("timeout", t);
  }

  /**
   * For further information about how/where this is used, see app.component.ts
   * @returns the current idle countdown in minutes
   */
  public getIdle(): string {
    if (!localStorage.getItem("idle")) localStorage.setItem('idle', "10");
    return localStorage.getItem("idle");
  }

  /**
   * @param i the new idle duration in minutes
   */
  public setIdle(i: string) {
    //using to notify subscriber components of the value that something has changed
    this.idleCounter.next(i);
    localStorage.setItem("idle", i);
  }

  /**
   * @returns an Observable that holds whether the current user is on mobile or not
   */
  public isMobile(): Observable<boolean> {
    return this._mobile.asObservable()
  }

  /*
   * private methods
   */

  /**
   * @returns whether the user uses a mobile/tablet device or not
   */
  private isMobileDevice(): boolean {
    //big big mobile device RegEx, for explanaiton why see the comments around the constructor
    let check: boolean = false;
    ((a) => {
      if (/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows ce|xda|xiino/i.test(a) || /1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(a.substr(0, 4)))
        check = true;
    })(navigator.userAgent || navigator.vendor);
    return check;
  }
}
