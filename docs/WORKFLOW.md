## 🧭 Übersicht

Dieses Projekt verwaltet Produkte, Lagerbestände, Einkaufsprozesse und zugehörige Stammdaten (z. B. Marken, Kategorien, Lieferanten).
Die zentrale Idee ist:
- **Produkte** werden zunächst ohne Lagerbestand oder Lieferantenbezug erstellt.
- **Brand** (Marke) und **Category** (Kategorie) sind Pflichtfelder.
- Lagerbestand wird ausschließlich durch den Abschluss eines **Einkaufs (Purchase)** gepflegt.
- Es gibt **Benutzer- und Rollenverwaltung**, sowie **aktiven Produktstatus**.
- Für die Verwaltung von Marken, Kategorien und Lieferanten gibt es eigene Interfaces.

---

## 🧩 Datenmodell

### 🔹 `products` 

- `id`, `name`, `description`, `sku`, `selling_price`, `cost_price`
- **Pflichtfelder:** `brand_id`, `category_id`
- **Optionale Felder:** `supplier_id`, Bilder, Attribute
- Beziehungen:
    - `brand_id` → `brands`
    - `category_id` → `categories`
    - `supplier_id` → `suppliers` _(erst bei Einkaufsabschluss gesetzt)_

### 🔹 `brands`, `categories`, `suppliers`

- Eigene Entitäten mit `id`, `name`, `created_at`, `updated_at`
- **Unique Constraints:** Name (bei `brands` & `categories`), Kombination aus `name` + `contact_email` bei `suppliers`

### 🔹 `stock` 

- Beziehung zwischen `product_id` & `warehouse_id`
- Menge (`quantity`) ≥ 0
- Primärschlüssel: Kombination aus `product_id`, `warehouse_id`

### 🔹 `stock_movements` 

- `movement_type`: `IN`, `OUT`, `RETURN`, `TRANSFER`, `DAMAGED`
- `reason`: z. B. `CREATED`, `DAMAGED`, `TRANSFERRED`, etc.
- Optionales Snapshot-Tracking (`product_name_snapshot`, `product_deleted`)

### 🔹 `warehouses`

- Lagerstandorte, genutzt für `stock`, `stock_movements`, `purchase_items`

### 🔹 `purchases` & `purchase_items`

- Einkauf mit Status:
    - `PENDING`: Einkaufsentwurf
    - `COMPLETED`: Lager wird befüllt, `stock` aktualisiert
    - `CANCELLED`: Produkte werden nicht übernommen, Status ggf. `DELETED`
- Produkte, Lager und Mengen über `purchase_items` zugeordnet

### 🔹 `attributes` & `product_attributes`

- Beliebige Attribute für Produkte (z. B. Farbe, Material)
- Beziehungstabelle mit `attribute_value`, `attribute_id`, `product_id`

### 🔹 `images`

- Optional mehrere Bilder pro Produkt

### 🔹 `users`, `roles`, `user_roles`

- Authentifizierung via `username`, `password`, `email`
- Rollenmodell (`ADMIN`, `USER`, etc.)

---

## 🔄 Workflow

### 1. Produkt anlegen

- Nur Stammdaten werden gespeichert
- **`brand_id`** und **`category_id`** müssen gesetzt werden
- Produkt-Status bleibt zunächst „inaktiv“
- Kein Lager, kein Supplier

### 2. Einkauf anlegen (`PENDING`)

- Produkte, Mengen, Lager auswählen
- Kein Lagerbestand wird verändert

### 3. Einkauf abschließen (`COMPLETED` / `CANCELLED`)

- `COMPLETED`:
    - `stock` wird erhöht
    - `stock_movements` mit `movement_type = IN`, `reason = CREATED`
    - `supplier_id` wird dem Produkt zugeordnet
    - Produkt-Status wird auf `ACTIVE` gesetzt
- `CANCELLED`:
    - Produkt-Status wird auf `DELETED` gesetzt (nicht gelöscht!)

### 4.  Bestandsänderungen außerhalb des Einkaufs

- Manuelle Korrekturen oder Anpassungen können über separate Prozesse erfolgen.
- Dabei können neue Lagerbewegungen (stock_movements) mit passenden Typen und Gründen erzeugt werden.
- Stammdaten, inklusive optionaler Felder, können bei Bedarf angepasst werden.
---

## 📌 Hinweise

- **Nur Produkte mit `status = ACTIVE` werden im Interface angezeigt**
- Die Erstellung von `brands`, `categories` und `suppliers` erfolgt über eigene Interfaces
- `brand_id` und `category_id` müssen auf gültige Einträge zeigen
- **Kein direkter Lagerzugriff**: Bestände werden nur über abgeschlossene Einkäufe gepflegt
- Validierungen:
    - Mengen und Preise ≥ 0
    - Eindeutige `sku` pro Produkt
    - Eindeutiger Name für Marken & Kategorien
