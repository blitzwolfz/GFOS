import { Mitarbeiter, Arbeitsgruppe } from './interfaces/default.model';

export const employeeSamples: Mitarbeiter[] = [
    {
        personalnummer: "01",
        name: "Test",
        vorname: "Auch",
        erreichbar: true,
        arbeitskonto: 0,
        email: "x@example.org",
        status: "string",
        rechteklasse: "root",
        abteilung: "string",
        vertreter: "string"
    },
    {
        personalnummer: "02",
        name: "Hmmmm",
        vorname: "Müller",
        erreichbar: true,
        arbeitskonto: 0,
        email: "x@example.org",
        status: "string",
        rechteklasse: "user",
        abteilung: "string",
        vertreter: "string"
    },
    {
        personalnummer: "02",
        name: "Hmmmm",
        vorname: "Müller",
        erreichbar: true,
        arbeitskonto: 0,
        email: "x@example.org",
        status: "string",
        rechteklasse: "user",
        abteilung: "string",
        vertreter: "string"
    },
    {
        personalnummer: "02",
        name: "Hmmmm",
        vorname: "Müller",
        erreichbar: true,
        arbeitskonto: 0,
        email: "x@example.org",
        status: "string",
        rechteklasse: "user",
        abteilung: "string",
        vertreter: "string"
    },
    {
        personalnummer: "02",
        name: "Hmmmm",
        vorname: "Müller",
        erreichbar: true,
        arbeitskonto: 0,
        email: "x@example.org",
        status: "string",
        rechteklasse: "user",
        abteilung: "string",
        vertreter: "string"
    }
];

export const groupSamples: Arbeitsgruppe[] = [
    {
        mitglieder: [
            "00000000001",
            "00000000002",
        ],
        leiter: "00000000001",
        bezeichnung: "Coole Gruppe",
        arbeitsgruppenID: "5"
    },
    {
        mitglieder: [
            "00000000001",
            "00000000002",
        ],
        leiter: "00000000001",
        bezeichnung: "Coole Gruppe2",
        arbeitsgruppenID: "53123"
    },
    {
        mitglieder: [
            "00000000001",
            "00000000002",
        ],
        leiter: "00000000001",
        bezeichnung: "Coole Gruppe324",
        arbeitsgruppenID: "512"
    }
]