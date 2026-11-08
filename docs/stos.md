# Stos ekranów

Stos rośnie ku górze, na wierzchu jest ekran widoczny dla użytkownika.

| Krok | Czynność | Stos po kroku |
|---|---|---|
| 0 | start aplikacji | `Main` |
| 1 | „Zamów" | `Summary` / `Main` |
| 2 | Wstecz | `Main` |
| 3 | „O aplikacji" | `About` / `Main` |
| 4 | Wstecz | `Main` |
| 5 | Wstecz | *(pusty, aplikacja się kończy)* |

```
 krok 0      krok 1       krok 2      krok 3      krok 4      krok 5
┌────────┐  ┌─────────┐  ┌────────┐  ┌────────┐  ┌────────┐
│  Main  │  │ Summary │  │  Main  │  │ About  │  │  Main  │   (pusty)
└────────┘  ├─────────┤  └────────┘  ├────────┤  └────────┘
            │  Main   │              │  Main  │
            └─────────┘              └────────┘
```

Wstecz zdejmuje wierzchni ekran. Gdy zdejmiemy ostatni, stos jest pusty i aplikacja się kończy.

## Logcat

```
(tu wklej fragment z własnego emulatora, filtr tag:STOS)

MainActivity onCreate
SummaryActivity onCreate
MainActivity onStop
SummaryActivity onStop
SummaryActivity onDestroy
AboutActivity onCreate
MainActivity onStop
AboutActivity onStop
AboutActivity onDestroy
MainActivity onStop
MainActivity onDestroy
```

## Obserwacje

- Przy przejściu na nowy ekran poprzedni dostaje `onStop`, ale **nie** `onDestroy`, bo czeka na stosie.
- Wstecz niszczy zdejmowany ekran (`onStop`, potem `onDestroy`), a ekran pod spodem tylko się wznawia.
- Ostatni Wstecz na `Main` niszczy go i kończy aplikację.
