import { Component, OnInit } from "@angular/core";
import { routerLinks } from "src/app/utils/interfaces/sidebar.model";
import { DataService } from "src/app/utils/services/data.service";
import {
  Mitarbeiter,
  Arbeitsgruppe
} from "src/app/utils/interfaces/default.model";
import { ApiService } from "src/app/utils/services/api.service";
import { interval } from "rxjs";

@Component({
  selector: "app-sidebar",
  templateUrl: "./sidebar.component.html",
  styleUrls: ["./sidebar.component.scss"]
})

export class SidebarComponent implements OnInit {
  //data about user and what to display in the sidebar
  user: Mitarbeiter;
  groups: Arbeitsgruppe[];

  //input for dynamic binding -> easier to maintain
  public sidebarLinks: Array<routerLinks> = [
    {
      title: "Allgemein",
      icon: "folder-open",
      iconWhenClosed: "folder",
      condition: "true",
      links: [
        {
          link: "/dashboard",
          title: "Dashboard",
          icon: "dashboard"
        },
        {
          link: "/settings",
          title: "Einstellungen",
          icon: "cog"
        },
        {
          link: "/employee/me",
          title: "Mein Profil",
          icon: "user"
        }
      ]
    }
  ];

  constructor(public dataService: DataService,
    public api: ApiService
  ) { }

  ngOnInit() {
    //check if user has permissions and display things based on that
    this.user = this.dataService.getUser();
    this.sidebarLinks[1] = {
      title: "Benutzerverwaltung",
      icon: "folder-open",
      iconWhenClosed: "folder",
      condition: '["admin", "root", "personnelDepartment"].includes(this.user?.rechteklasse)',
      links: [
        {
          link: "/register",
          title: "Benutzer hinzufügen",
          icon: "assign-user"
        },
        {
          link: "/delete",
          title: "Benutzer löschen",
          icon: "times-circle"
        },
        {
          link: "/view-all",
          title: "Alle Benutzer sehen",
          icon: "users"
        }
      ]
    };

    this.dataService.getGroups(true).subscribe(g => (this.groups = g));
  }

  public changeIcon(index: number, toggle: boolean): void {
    if (!this.sidebarLinks[index].iconWhenClosed) return;
    //swap the two values -> change of animation in the sidebar
    [this.sidebarLinks[index].icon, this.sidebarLinks[index].iconWhenClosed] = [
      this.sidebarLinks[index].iconWhenClosed,
      this.sidebarLinks[index].icon
    ];
  }

  //workaround for conditions since subscribing won't work if object isn't initialized at start
  //which isn't the case because the user isn't logged in at the beginning
  public parseCondition(str: string){
    return eval(str);
  }
}
