package drinkshop.repository.file;

import drinkshop.domain.Product;
import drinkshop.domain.CategorieBautura;
import drinkshop.domain.TipBautura;

public class FileProductRepository
        extends FileAbstractRepository<Integer, Product> {

    // C11: Errors are made in writing out variable names
    // Observatie: Folosim constante pentru a clarifica semnificatia indicilor CSV
    private static final int IDX_ID = 0;
    private static final int IDX_NAME = 1;
    private static final int IDX_PRICE = 2;
    private static final int IDX_CATEGORY = 3;
    private static final int IDX_TYPE = 4;
    private static final int EXPECTED_COLUMNS = 5;

    public FileProductRepository(String fileName) {
        super(fileName);
        loadFromFile();
    }

    @Override
    protected Integer getId(Product entity) {
        return entity.getId();
    }

    @Override
    protected Product extractEntity(String line) {

        String[] elems = line.split(",");

        // C06: There are errors in preparing or processing input data
        // Observatie: Validam datele de intrare pentru a preveni crash-uri pe linii incomplete
        if (elems.length < EXPECTED_COLUMNS) {
            System.err.println("Linie invalida ignorata: " + line);
            return null; // Sau aruncam exceptie, depinde de politica de erori
        }

        try {
            int id = Integer.parseInt(elems[IDX_ID]);
            String name = elems[IDX_NAME];
            double price = Double.parseDouble(elems[IDX_PRICE]);
            CategorieBautura categorie = CategorieBautura.valueOf(elems[IDX_CATEGORY]);
            TipBautura tip = TipBautura.valueOf(elems[IDX_TYPE]);

            return new Product(id, name, price, categorie, tip);
        } catch (IllegalArgumentException e) {
            // C08: Error message processing errors exist (Gestionare locala)
            System.err.println("Eroare la parsarea produsului: " + line);
            return null;
        }
    }

    @Override
    protected String createEntityAsString(Product entity) {
        return entity.getId() + "," +
                entity.getNume() + "," +
                entity.getPret() + "," +
                entity.getCategorie() + "," +
                entity.getTip();
    }
}