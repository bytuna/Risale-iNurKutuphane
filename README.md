# Kütüphane (Android • Kotlin + Jetpack Compose)

Ahşap raflı, açılır/kapanır başlıklı, dikey kaydırılabilir kitaplık uygulaması.
Kitaba dokununca `.txt` içeriği hamur kâğıt görünümlü sayfalara bölünerek okunur.

## Çalıştırma

1. Android Studio'da **File → Open** ile bu `Kutuphane` klasörünü açın.
2. Gradle senkronizasyonu bitince telefon/emülatör seçip ▶ ile çalıştırın.
   (Android Studio eski/yeni sürüm uyarısı verirse "Update" diyerek AGP ve Kotlin sürümlerini güncelleyebilirsiniz.)

## Kitap, kapak ve raf ekleme

Hepsi `app/src/main/assets/` altındadır:

| Ne                | Nereye                                  |
|-------------------|-----------------------------------------|
| Kapak resimleri   | `assets/covers/` (jpg/png, öneri 2:3, örn. 600×900) |
| Kitap metinleri   | `assets/books/` (**UTF-8** `.txt`)      |
| Raf/kitap listesi | `assets/library.json`                   |

`library.json` örneği:

```json
{
  "shelves": [
    {
      "title": "Muhtelif Eserler",
      "open": true,
      "books": [
        { "id": "kitap-1", "title": "Kitap Adı", "cover": "covers/kitap-1.jpg", "text": "books/kitap-1.txt" }
      ]
    }
  ]
}
```

- `id` her kitap için benzersiz olmalı (okuma konumu bu id ile saklanır).
- `open: false` yazarsanız raf ilk açılışta kapalı gelir. Kitabı olmayan raf "Raf boş" gösterir.
- Kapak dosyası bulunamazsa kitap adıyla otomatik bir kapak çizilir.
- `.txt` dosyaları UTF-8 kaydedilmeli (Türkçe karakterler için). Paragraflar ayrı satır olmalı;
  her 80 karakterde sert satır sonu varsa metin düzensiz görünür.

## Özellikler

- Ahşap doku koddan üretilir (ekstra resim gerekmez); raf başlığına dokununca raf açılır/kapanır, durum hatırlanır.
- Sol üst hamburger menü: şimdilik örnek maddeler (`LibraryDrawer` içinde güncellenecek).
- Sağ üst arama: kitap adına göre filtreler.
- Kitap sağ üstündeki üç nokta: "Oku" / "Başa dön".
- Okuyucu: sayfalar yatay kaydırılır, sayfa numarası altta; üstte A−/A+ ile yazı boyutu, "12/340" düğmesiyle sayfaya git.
- Okuma konumu karakter indeksi olarak saklanır; yazı boyutu değişse bile aynı yerden devam edilir.

## Kod haritası

- `Data.kt` – modeller, `library.json` okuma, kapak/metin yükleme, ayar saklama
- `ui/LibraryScreen.kt` – raflar, başlıklar, hamburger panel, arama
- `ui/WoodTextures.kt` – ahşap ve hamur kâğıt dokuları
- `ui/BookCover.kt` – kapak resmi / otomatik kapak
- `ui/Paginator.kt` – metni sayfalara bölme
- `ui/ReaderScreen.kt` – okuyucu
