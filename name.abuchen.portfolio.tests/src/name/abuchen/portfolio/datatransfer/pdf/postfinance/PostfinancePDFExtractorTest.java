package name.abuchen.portfolio.datatransfer.pdf.postfinance;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import name.abuchen.portfolio.datatransfer.Extractor;
import name.abuchen.portfolio.datatransfer.Extractor.BuySellEntryItem;
import name.abuchen.portfolio.datatransfer.Extractor.Item;
import name.abuchen.portfolio.datatransfer.Extractor.SecurityItem;
import name.abuchen.portfolio.datatransfer.Extractor.TransactionItem;
import name.abuchen.portfolio.datatransfer.actions.AssertImportActions;
import name.abuchen.portfolio.datatransfer.pdf.PDFInputFile;
import name.abuchen.portfolio.datatransfer.pdf.PostfinancePDFExtractor;
import name.abuchen.portfolio.model.AccountTransaction;
import name.abuchen.portfolio.model.BuySellEntry;
import name.abuchen.portfolio.model.Client;
import name.abuchen.portfolio.model.PortfolioTransaction;
import name.abuchen.portfolio.model.Security;
import name.abuchen.portfolio.model.Transaction.Unit;
import name.abuchen.portfolio.money.CurrencyUnit;
import name.abuchen.portfolio.money.Money;
import name.abuchen.portfolio.money.Values;

public class PostfinancePDFExtractorTest
{
    @Test
    public void testKauf01()
    {
        Client client = new Client();

        PostfinancePDFExtractor extractor = new PostfinancePDFExtractor(client);

        List<Exception> errors = new ArrayList<>();

        List<Item> results = extractor.extract(PDFInputFile.loadTestCase(getClass(), "Kauf01.txt"), errors);

        assertThat(errors, empty());
        assertThat(results.size(), is(2));
        new AssertImportActions().check(results, CurrencyUnit.EUR);

        // check security
        Security security = results.stream().filter(i -> i instanceof SecurityItem).findFirst()
                        .orElseThrow(IllegalArgumentException::new).getSecurity();
        assertThat(security.getIsin(), is("NL0000009355"));
        assertThat(security.getName(), is("UNILEVER DUTCH CERT"));
        assertThat(security.getCurrencyCode(), is(CurrencyUnit.EUR));

        // check buy sell transaction
        BuySellEntry entry = (BuySellEntry) results.stream().filter(BuySellEntryItem.class::isInstance)
                        .collect(Collectors.toList()).get(0).getSubject();

        assertThat(entry.getPortfolioTransaction().getType(), is(PortfolioTransaction.Type.BUY));
        assertThat(entry.getAccountTransaction().getType(), is(AccountTransaction.Type.BUY));

        assertThat(entry.getPortfolioTransaction().getDateTime(), is(LocalDateTime.parse("2018-09-25T00:00")));
        assertThat(entry.getPortfolioTransaction().getShares(), is(Values.Share.factorize(60)));
        assertThat(entry.getSource(), is("Kauf01.txt"));
        assertThat(entry.getNote(), is("Referenz: 153557048"));

        assertThat(entry.getPortfolioTransaction().getMonetaryAmount(),
                        is(Money.of(CurrencyUnit.EUR, Values.Amount.factorize(2850.24))));
        assertThat(entry.getPortfolioTransaction().getGrossValue(),
                        is(Money.of(CurrencyUnit.EUR, Values.Amount.factorize(2837.40))));
        assertThat(entry.getPortfolioTransaction().getUnitSum(Unit.Type.TAX),
                        is(Money.of(CurrencyUnit.EUR, Values.Amount.factorize(4.26))));
        assertThat(entry.getPortfolioTransaction().getUnitSum(Unit.Type.FEE),
                        is(Money.of(CurrencyUnit.EUR, Values.Amount.factorize(8.58))));
    }

    @Test
    public void testKauf02()
    {
        Client client = new Client();

        PostfinancePDFExtractor extractor = new PostfinancePDFExtractor(client);

        List<Exception> errors = new ArrayList<>();

        List<Item> results = extractor.extract(PDFInputFile.loadTestCase(getClass(), "Kauf02.txt"), errors);

        assertThat(errors, empty());
        assertThat(results.size(), is(2));
        new AssertImportActions().check(results, "CHF");

        // check security
        Security security = results.stream().filter(i -> i instanceof SecurityItem).findFirst()
                        .orElseThrow(IllegalArgumentException::new).getSecurity();
        assertThat(security.getIsin(), is("CH0025751329"));
        assertThat(security.getName(), is("LOGITECH N"));
        assertThat(security.getCurrencyCode(), is("CHF"));

        // check buy sell transaction
        BuySellEntry entry = (BuySellEntry) results.stream().filter(BuySellEntryItem.class::isInstance)
                        .collect(Collectors.toList()).get(0).getSubject();

        assertThat(entry.getPortfolioTransaction().getType(), is(PortfolioTransaction.Type.BUY));
        assertThat(entry.getAccountTransaction().getType(), is(AccountTransaction.Type.BUY));

        assertThat(entry.getPortfolioTransaction().getDateTime(), is(LocalDateTime.parse("2018-10-25T00:00")));
        assertThat(entry.getPortfolioTransaction().getShares(), is(Values.Share.factorize(87)));
        assertThat(entry.getSource(), is("Kauf02.txt"));
        assertThat(entry.getNote(), is("Referenz: 155571206"));

        assertThat(entry.getPortfolioTransaction().getMonetaryAmount(),
                        is(Money.of("CHF", Values.Amount.factorize(2998.95))));
        assertThat(entry.getPortfolioTransaction().getGrossValue(),
                        is(Money.of("CHF", Values.Amount.factorize(2970.20))));
        assertThat(entry.getPortfolioTransaction().getUnitSum(Unit.Type.TAX),
                        is(Money.of("CHF", Values.Amount.factorize(2.25))));
        assertThat(entry.getPortfolioTransaction().getUnitSum(Unit.Type.FEE),
                        is(Money.of("CHF", Values.Amount.factorize(25.00 + 1.50))));
    }

    @Test
    public void testKauf03()
    {
        Client client = new Client();

        PostfinancePDFExtractor extractor = new PostfinancePDFExtractor(client);

        List<Exception> errors = new ArrayList<>();

        List<Item> results = extractor.extract(PDFInputFile.loadTestCase(getClass(), "Kauf03.txt"), errors);

        assertThat(errors, empty());
        assertThat(results.size(), is(2));
        new AssertImportActions().check(results, "CHF");

        // check security
        Security security = results.stream().filter(i -> i instanceof SecurityItem).findFirst()
                        .orElseThrow(IllegalArgumentException::new).getSecurity();
        assertThat(security.getIsin(), is("DE000PAH0038"));
        assertThat(security.getName(), is("PORSCHE AUTOMOBIL HOLDING PRF"));
        assertThat(security.getCurrencyCode(), is(CurrencyUnit.EUR));

        // check buy sell transaction
        BuySellEntry entry = (BuySellEntry) results.stream().filter(BuySellEntryItem.class::isInstance)
                        .collect(Collectors.toList()).get(0).getSubject();

        assertThat(entry.getPortfolioTransaction().getType(), is(PortfolioTransaction.Type.BUY));
        assertThat(entry.getAccountTransaction().getType(), is(AccountTransaction.Type.BUY));

        assertThat(entry.getPortfolioTransaction().getDateTime(), is(LocalDateTime.parse("2017-03-27T00:00")));
        assertThat(entry.getPortfolioTransaction().getShares(), is(Values.Share.factorize(55)));
        assertThat(entry.getSource(), is("Kauf03.txt"));
        assertThat(entry.getNote(), is("Referenz: 122007844"));

        assertThat(entry.getPortfolioTransaction().getMonetaryAmount(),
                        is(Money.of("CHF", Values.Amount.factorize(2968.50))));
        assertThat(entry.getPortfolioTransaction().getGrossValue(),
                        is(Money.of("CHF", Values.Amount.factorize(2963.50))));
        assertThat(entry.getPortfolioTransaction().getUnitSum(Unit.Type.TAX),
                        is(Money.of("CHF", Values.Amount.factorize(4.45))));
        assertThat(entry.getPortfolioTransaction().getUnitSum(Unit.Type.FEE),
                        is(Money.of("CHF", Values.Amount.factorize(0.60 / 1.08279))));

        Unit grossValueUnit = entry.getPortfolioTransaction().getUnit(Unit.Type.GROSS_VALUE).orElseThrow(IllegalArgumentException::new);
        assertThat(grossValueUnit.getForex(),
                        is(Money.of(CurrencyUnit.EUR, Values.Amount.factorize(2737.40))));
    }

    @Test
    public void testKauf04()
    {
        Client client = new Client();

        PostfinancePDFExtractor extractor = new PostfinancePDFExtractor(client);

        List<Exception> errors = new ArrayList<>();

        List<Item> results = extractor.extract(PDFInputFile.loadTestCase(getClass(), "Kauf04.txt"), errors);

        assertThat(errors, empty());
        assertThat(results.size(), is(2));
        new AssertImportActions().check(results, "CHF");

        // check security
        Security security = results.stream().filter(i -> i instanceof SecurityItem).findFirst()
                        .orElseThrow(IllegalArgumentException::new).getSecurity();
        assertThat(security.getIsin(), is("LU0188802960"));
        assertThat(security.getName(), is("Pictet - Japan Index - I"));
        assertThat(security.getCurrencyCode(), is("JPY"));

        // check buy sell transaction
        BuySellEntry entry = (BuySellEntry) results.stream().filter(BuySellEntryItem.class::isInstance)
                        .collect(Collectors.toList()).get(0).getSubject();

        assertThat(entry.getPortfolioTransaction().getType(), is(PortfolioTransaction.Type.BUY));
        assertThat(entry.getAccountTransaction().getType(), is(AccountTransaction.Type.BUY));

        assertThat(entry.getPortfolioTransaction().getDateTime(), is(LocalDateTime.parse("2021-12-20T00:00")));
        assertThat(entry.getPortfolioTransaction().getShares(), is(Values.Share.factorize(1.441)));
        assertThat(entry.getSource(), is("Kauf04.txt"));
        assertThat(entry.getNote(), is("Auftrag 10111111"));

        assertThat(entry.getPortfolioTransaction().getMonetaryAmount(),
                        is(Money.of("CHF", Values.Amount.factorize(280.91))));
        assertThat(entry.getPortfolioTransaction().getGrossValue(),
                        is(Money.of("CHF", Values.Amount.factorize(280.49))));
        assertThat(entry.getPortfolioTransaction().getUnitSum(Unit.Type.TAX),
                        is(Money.of("CHF", Values.Amount.factorize(0.00))));
        assertThat(entry.getPortfolioTransaction().getUnitSum(Unit.Type.FEE),
                        is(Money.of("CHF", Values.Amount.factorize(0.42))));

        Unit grossValueUnit = entry.getPortfolioTransaction().getUnit(Unit.Type.GROSS_VALUE).orElseThrow(IllegalArgumentException::new);
        assertThat(grossValueUnit.getForex(),
                        is(Money.of("JPY", Values.Amount.factorize(34070.00))));
    }

    @Test
    public void testVerkauf01()
    {
        Client client = new Client();

        PostfinancePDFExtractor extractor = new PostfinancePDFExtractor(client);

        List<Exception> errors = new ArrayList<>();

        List<Item> results = extractor.extract(PDFInputFile.loadTestCase(getClass(), "Verkauf01.txt"), errors);

        assertThat(errors, empty());
        assertThat(results.size(), is(2));
        new AssertImportActions().check(results, "CHF");

        // check security
        Security security = results.stream().filter(i -> i instanceof SecurityItem).findFirst()
                        .orElseThrow(IllegalArgumentException::new).getSecurity();
        assertThat(security.getIsin(), is("IE00BD4TYL27"));
        assertThat(security.getName(), is("UBSETF MSCI USA hdg to CHF"));
        assertThat(security.getCurrencyCode(), is("CHF"));

        // check buy sell transaction
        BuySellEntry entry = (BuySellEntry) results.stream().filter(BuySellEntryItem.class::isInstance)
                        .collect(Collectors.toList()).get(0).getSubject();

        assertThat(entry.getPortfolioTransaction().getType(), is(PortfolioTransaction.Type.SELL));
        assertThat(entry.getAccountTransaction().getType(), is(AccountTransaction.Type.SELL));

        assertThat(entry.getPortfolioTransaction().getDateTime(), is(LocalDateTime.parse("2018-09-20T00:00")));
        assertThat(entry.getPortfolioTransaction().getShares(), is(Values.Share.factorize(308)));
        assertThat(entry.getSource(), is("Verkauf01.txt"));
        assertThat(entry.getNote(), is("Referenz: 153327044"));

        assertThat(entry.getPortfolioTransaction().getMonetaryAmount(),
                        is(Money.of("CHF", Values.Amount.factorize(7467.50))));
        assertThat(entry.getPortfolioTransaction().getGrossValue(),
                        is(Money.of("CHF", Values.Amount.factorize(7481.30))));
        assertThat(entry.getPortfolioTransaction().getUnitSum(Unit.Type.TAX),
                        is(Money.of("CHF", Values.Amount.factorize(11.20))));
        assertThat(entry.getPortfolioTransaction().getUnitSum(Unit.Type.FEE),
                        is(Money.of("CHF", Values.Amount.factorize(2.60))));
    }

    @Test
    public void testDividende01()
    {
        PostfinancePDFExtractor extractor = new PostfinancePDFExtractor(new Client());

        List<Exception> errors = new ArrayList<>();

        List<Item> results = extractor.extract(PDFInputFile.loadTestCase(getClass(), "Dividende01.txt"), errors);

        assertThat(errors, empty());
        assertThat(results.size(), is(2));
        new AssertImportActions().check(results, CurrencyUnit.EUR);

        // check security
        Security security = results.stream().filter(SecurityItem.class::isInstance).findFirst()
                        .orElseThrow(IllegalArgumentException::new).getSecurity();
        assertThat(security.getIsin(), is("NL0000009355"));
        assertThat(security.getName(), is("UNILEVER DUTCH CERT"));
        assertThat(security.getCurrencyCode(), is(CurrencyUnit.EUR));

        // check dividends transaction
        AccountTransaction transaction = (AccountTransaction) results.stream().filter(TransactionItem.class::isInstance)
                        .findFirst().orElseThrow(IllegalArgumentException::new).getSubject();

        assertThat(transaction.getType(), is(AccountTransaction.Type.DIVIDENDS));

        assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2019-06-05T00:00")));
        assertThat(transaction.getShares(), is(Values.Share.factorize(60)));
        assertThat(transaction.getSource(), is("Dividende01.txt"));
        assertThat(transaction.getNote(), is("Referenz: 169933304"));

        assertThat(transaction.getMonetaryAmount(),
                        is(Money.of(CurrencyUnit.EUR, Values.Amount.factorize(20.93))));
        assertThat(transaction.getGrossValue(),
                        is(Money.of(CurrencyUnit.EUR, Values.Amount.factorize(24.62))));
        assertThat(transaction.getUnitSum(Unit.Type.TAX),
                        is(Money.of(CurrencyUnit.EUR, Values.Amount.factorize(3.69))));
        assertThat(transaction.getUnitSum(Unit.Type.FEE),
                        is(Money.of(CurrencyUnit.EUR, Values.Amount.factorize(0.00))));
    }

    @Test
    public void testDividende02()
    {
        PostfinancePDFExtractor extractor = new PostfinancePDFExtractor(new Client());

        List<Exception> errors = new ArrayList<>();

        List<Item> results = extractor.extract(PDFInputFile.loadTestCase(getClass(), "Dividende02.txt"), errors);

        assertThat(errors, empty());
        assertThat(results.size(), is(2));
        new AssertImportActions().check(results, "CHF");

        // check security
        Security security = results.stream().filter(SecurityItem.class::isInstance).findFirst()
                        .orElseThrow(IllegalArgumentException::new).getSecurity();
        assertThat(security.getIsin(), is("CH0032912732"));
        assertThat(security.getName(), is("UBS ETF CH - SLI CHF A"));
        assertThat(security.getCurrencyCode(), is("CHF"));

        // check dividends transaction
        AccountTransaction transaction = (AccountTransaction) results.stream().filter(TransactionItem.class::isInstance)
                        .findFirst().orElseThrow(IllegalArgumentException::new).getSubject();

        assertThat(transaction.getType(), is(AccountTransaction.Type.DIVIDENDS));

        assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2017-09-08T00:00")));
        assertThat(transaction.getShares(), is(Values.Share.factorize(34)));
        assertThat(transaction.getSource(), is("Dividende02.txt"));
        assertThat(transaction.getNote(), is("Referenz: 130516418"));

        assertThat(transaction.getMonetaryAmount(),
                        is(Money.of("CHF", Values.Amount.factorize(36.69))));
        assertThat(transaction.getGrossValue(),
                        is(Money.of("CHF", Values.Amount.factorize(56.44))));
        assertThat(transaction.getUnitSum(Unit.Type.TAX),
                        is(Money.of("CHF", Values.Amount.factorize(19.75))));
        assertThat(transaction.getUnitSum(Unit.Type.FEE),
                        is(Money.of("CHF", Values.Amount.factorize(0.00))));
    }

    @Test
    public void testDividende03()
    {
        PostfinancePDFExtractor extractor = new PostfinancePDFExtractor(new Client());

        List<Exception> errors = new ArrayList<>();

        List<Item> results = extractor.extract(PDFInputFile.loadTestCase(getClass(), "Dividende03.txt"), errors);

        assertThat(errors, empty());
        assertThat(results.size(), is(2));
        new AssertImportActions().check(results, "CHF");

        // check security
        Security security = results.stream().filter(SecurityItem.class::isInstance).findFirst()
                        .orElseThrow(IllegalArgumentException::new).getSecurity();
        assertThat(security.getIsin(), is("CH0019396990"));
        assertThat(security.getName(), is("YPSOMED HLDG"));
        assertThat(security.getCurrencyCode(), is("CHF"));

        // check dividends transaction
        AccountTransaction transaction = (AccountTransaction) results.stream().filter(TransactionItem.class::isInstance)
                        .findFirst().orElseThrow(IllegalArgumentException::new).getSubject();

        assertThat(transaction.getType(), is(AccountTransaction.Type.DIVIDENDS));

        assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-07-05T00:00")));
        assertThat(transaction.getShares(), is(Values.Share.factorize(19)));
        assertThat(transaction.getSource(), is("Dividende03.txt"));
        assertThat(transaction.getNote(), is("Referenz: 149619136"));

        assertThat(transaction.getMonetaryAmount(),
                        is(Money.of("CHF", Values.Amount.factorize(26.60))));
        assertThat(transaction.getGrossValue(),
                        is(Money.of("CHF", Values.Amount.factorize(26.60))));
        assertThat(transaction.getUnitSum(Unit.Type.TAX),
                        is(Money.of("CHF", Values.Amount.factorize(0.00))));
        assertThat(transaction.getUnitSum(Unit.Type.FEE),
                        is(Money.of("CHF", Values.Amount.factorize(0.00))));
    }

    @Test
    public void testJahresgebuehr01()
    {
        PostfinancePDFExtractor extractor = new PostfinancePDFExtractor(new Client());

        List<Exception> errors = new ArrayList<>();

        List<Item> results = extractor.extract(PDFInputFile.loadTestCase(getClass(), "Jahresgebuehr01.txt"), errors);

        assertThat(errors, empty());
        assertThat(results.size(), is(1));
        new AssertImportActions().check(results, "CHF");

        // check fees transaction
        AccountTransaction transaction = (AccountTransaction) results.stream().filter(TransactionItem.class::isInstance)
                        .findFirst().orElseThrow(IllegalArgumentException::new).getSubject();

        assertThat(transaction.getType(), is(AccountTransaction.Type.FEES));

        assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2019-01-03T00:00")));
        assertThat(transaction.getSource(), is("Jahresgebuehr01.txt"));
        assertThat(transaction.getNote(), is("Jahresgebühr - Referenz: 161333839"));

        assertThat(transaction.getMonetaryAmount(),
                        is(Money.of("CHF", Values.Amount.factorize(90.00))));
        assertThat(transaction.getGrossValue(),
                        is(Money.of("CHF", Values.Amount.factorize(90.00))));
        assertThat(transaction.getUnitSum(Unit.Type.TAX),
                        is(Money.of("CHF", Values.Amount.factorize(0.00))));
        assertThat(transaction.getUnitSum(Unit.Type.FEE),
                        is(Money.of("CHF", Values.Amount.factorize(0.00))));
    }

    @Test
    public void testZinsabschluss01()
    {
        PostfinancePDFExtractor extractor = new PostfinancePDFExtractor(new Client());

        List<Exception> errors = new ArrayList<>();

        List<Item> results = extractor.extract(PDFInputFile.loadTestCase(getClass(), "Zinsabschluss01.txt"), errors);

        assertThat(errors, empty());
        assertThat(results.size(), is(2));
        new AssertImportActions().check(results, "CHF");

        // check 1st interest transaction
        AccountTransaction transaction = (AccountTransaction) results.stream().filter(TransactionItem.class::isInstance)
                        .findFirst().orElseThrow(IllegalArgumentException::new).getSubject();

        assertThat(transaction.getType(), is(AccountTransaction.Type.INTEREST));

        assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2019-10-31T00:00")));
        assertThat(transaction.getSource(), is("Zinsabschluss01.txt"));
        assertThat(transaction.getNote(), is("01.01.2019 – 31.10.2019"));

        assertThat(transaction.getMonetaryAmount(),
                        is(Money.of("CHF", Values.Amount.factorize(4.17))));
        assertThat(transaction.getGrossValue(),
                        is(Money.of("CHF", Values.Amount.factorize(4.17))));
        assertThat(transaction.getUnitSum(Unit.Type.TAX),
                        is(Money.of("CHF", Values.Amount.factorize(0.00))));
        assertThat(transaction.getUnitSum(Unit.Type.FEE),
                        is(Money.of("CHF", Values.Amount.factorize(0.00))));

        // check 2nd interest transaction
        transaction = (AccountTransaction) results.stream().filter(TransactionItem.class::isInstance).skip(1)
                        .findFirst().orElseThrow(IllegalArgumentException::new).getSubject();

        assertThat(transaction.getType(), is(AccountTransaction.Type.INTEREST));

        assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2019-12-31T00:00")));
        assertThat(transaction.getSource(), is("Zinsabschluss01.txt"));
        assertThat(transaction.getNote(), is("01.11.2019 – 31.12.2019"));

        assertThat(transaction.getMonetaryAmount(),
                        is(Money.of("CHF", Values.Amount.factorize(0.42))));
        assertThat(transaction.getGrossValue(),
                        is(Money.of("CHF", Values.Amount.factorize(0.42))));
        assertThat(transaction.getUnitSum(Unit.Type.TAX),
                        is(Money.of("CHF", Values.Amount.factorize(0.00))));
        assertThat(transaction.getUnitSum(Unit.Type.FEE),
                        is(Money.of("CHF", Values.Amount.factorize(0.00))));
    }

    @Test
    public void testZinsabschluss02()
    {
        PostfinancePDFExtractor extractor = new PostfinancePDFExtractor(new Client());

        List<Exception> errors = new ArrayList<>();

        List<Item> results = extractor.extract(PDFInputFile.loadTestCase(getClass(), "Zinsabschluss02.txt"), errors);

        assertThat(errors, empty());
        assertThat(results.size(), is(3));
        new AssertImportActions().check(results, "CHF");

        // check 1st interest transaction
        AccountTransaction transaction = (AccountTransaction) results.stream().filter(TransactionItem.class::isInstance)
                        .findFirst().orElseThrow(IllegalArgumentException::new).getSubject();

        assertThat(transaction.getType(), is(AccountTransaction.Type.INTEREST));

        assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2019-10-31T00:00")));
        assertThat(transaction.getSource(), is("Zinsabschluss02.txt"));
        assertThat(transaction.getNote(), is("01.01.2019 – 31.10.2019"));

        assertThat(transaction.getMonetaryAmount(),
                        is(Money.of("CHF", Values.Amount.factorize(83.33))));
        assertThat(transaction.getGrossValue(),
                        is(Money.of("CHF", Values.Amount.factorize(83.33))));
        assertThat(transaction.getUnitSum(Unit.Type.TAX),
                        is(Money.of("CHF", Values.Amount.factorize(0.00))));
        assertThat(transaction.getUnitSum(Unit.Type.FEE),
                        is(Money.of("CHF", Values.Amount.factorize(0.00))));

        // check 2nd interest transaction
        transaction = (AccountTransaction) results.stream().filter(TransactionItem.class::isInstance).skip(1)
                        .findFirst().orElseThrow(IllegalArgumentException::new).getSubject();

        assertThat(transaction.getType(), is(AccountTransaction.Type.INTEREST));

        assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2019-12-31T00:00")));
        assertThat(transaction.getSource(), is("Zinsabschluss02.txt"));
        assertThat(transaction.getNote(), is("01.11.2019 – 31.12.2019"));

        assertThat(transaction.getMonetaryAmount(),
                        is(Money.of("CHF", Values.Amount.factorize(33.33))));
        assertThat(transaction.getGrossValue(),
                        is(Money.of("CHF", Values.Amount.factorize(33.33))));
        assertThat(transaction.getUnitSum(Unit.Type.TAX),
                        is(Money.of("CHF", Values.Amount.factorize(0.00))));
        assertThat(transaction.getUnitSum(Unit.Type.FEE),
                        is(Money.of("CHF", Values.Amount.factorize(0.00))));

        // check taxes transaction
        transaction = (AccountTransaction) results.stream().filter(TransactionItem.class::isInstance).skip(2)
                        .findFirst().orElseThrow(IllegalArgumentException::new).getSubject();

        assertThat(transaction.getType(), is(AccountTransaction.Type.TAXES));

        assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2019-12-31T00:00")));
        assertThat(transaction.getSource(), is("Zinsabschluss02.txt"));
        assertThat(transaction.getNote(), is("Verrechnungssteuer 01.01.2019 - 31.12.2019"));

        assertThat(transaction.getMonetaryAmount(),
                        is(Money.of("CHF", Values.Amount.factorize(40.83))));
        assertThat(transaction.getGrossValue(),
                        is(Money.of("CHF", Values.Amount.factorize(40.83))));
        assertThat(transaction.getUnitSum(Unit.Type.TAX),
                        is(Money.of("CHF", Values.Amount.factorize(0.00))));
        assertThat(transaction.getUnitSum(Unit.Type.FEE),
                        is(Money.of("CHF", Values.Amount.factorize(0.00))));
    }

    @Test
    public void testZinsabschluss03()
    {
        PostfinancePDFExtractor extractor = new PostfinancePDFExtractor(new Client());

        List<Exception> errors = new ArrayList<>();

        List<Item> results = extractor.extract(PDFInputFile.loadTestCase(getClass(), "Zinsabschluss03.txt"), errors);

        assertThat(errors, empty());
        assertThat(results.size(), is(2));
        new AssertImportActions().check(results, "CHF");

        // check interest transaction
        AccountTransaction transaction = (AccountTransaction) results.stream().filter(TransactionItem.class::isInstance)
                        .findFirst().orElseThrow(IllegalArgumentException::new).getSubject();

        assertThat(transaction.getType(), is(AccountTransaction.Type.INTEREST));

        assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-12-31T00:00")));
        assertThat(transaction.getSource(), is("Zinsabschluss03.txt"));
        assertThat(transaction.getNote(), is("01.01.2015 - 31.12.2015"));

        assertThat(transaction.getMonetaryAmount(),
                        is(Money.of("CHF", Values.Amount.factorize(1.00))));
        assertThat(transaction.getGrossValue(),
                        is(Money.of("CHF", Values.Amount.factorize(1.00))));
        assertThat(transaction.getUnitSum(Unit.Type.TAX),
                        is(Money.of("CHF", Values.Amount.factorize(0.00))));
        assertThat(transaction.getUnitSum(Unit.Type.FEE),
                        is(Money.of("CHF", Values.Amount.factorize(0.00))));

        // check fees transaction
        transaction = (AccountTransaction) results.stream().filter(TransactionItem.class::isInstance).skip(1)
                        .findFirst().orElseThrow(IllegalArgumentException::new).getSubject();

        assertThat(transaction.getType(), is(AccountTransaction.Type.FEES));

        assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-12-31T00:00")));
        assertThat(transaction.getSource(), is("Zinsabschluss03.txt"));
        assertThat(transaction.getNote(), is("Gebührenausweis 01.01.2015 - 31.12.2015"));

        assertThat(transaction.getMonetaryAmount(),
                        is(Money.of("CHF", Values.Amount.factorize(60.00))));
        assertThat(transaction.getGrossValue(),
                        is(Money.of("CHF", Values.Amount.factorize(60.00))));
        assertThat(transaction.getUnitSum(Unit.Type.TAX),
                        is(Money.of("CHF", Values.Amount.factorize(0.00))));
        assertThat(transaction.getUnitSum(Unit.Type.FEE),
                        is(Money.of("CHF", Values.Amount.factorize(0.00))));
    }

    @Test
    public void testZinsabschluss04()
    {
        PostfinancePDFExtractor extractor = new PostfinancePDFExtractor(new Client());

        List<Exception> errors = new ArrayList<>();

        List<Item> results = extractor.extract(PDFInputFile.loadTestCase(getClass(), "Zinsabschluss04.txt"), errors);

        assertThat(errors, empty());
        assertThat(results.size(), is(3));
        new AssertImportActions().check(results, "CHF");

        // check interest transaction
        AccountTransaction transaction = (AccountTransaction) results.stream().filter(TransactionItem.class::isInstance)
                        .findFirst().orElseThrow(IllegalArgumentException::new).getSubject();

        assertThat(transaction.getType(), is(AccountTransaction.Type.INTEREST));

        assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2017-12-31T00:00")));
        assertThat(transaction.getSource(), is("Zinsabschluss04.txt"));
        assertThat(transaction.getNote(), is("01.01.2017 - 31.12.2017"));

        assertThat(transaction.getMonetaryAmount(),
                        is(Money.of("CHF", Values.Amount.factorize(400.00))));
        assertThat(transaction.getGrossValue(),
                        is(Money.of("CHF", Values.Amount.factorize(400.00))));
        assertThat(transaction.getUnitSum(Unit.Type.TAX),
                        is(Money.of("CHF", Values.Amount.factorize(0.00))));
        assertThat(transaction.getUnitSum(Unit.Type.FEE),
                        is(Money.of("CHF", Values.Amount.factorize(0.00))));

        // check taxes transaction
        transaction = (AccountTransaction) results.stream().filter(TransactionItem.class::isInstance).skip(1)
                        .findFirst().orElseThrow(IllegalArgumentException::new).getSubject();

        assertThat(transaction.getType(), is(AccountTransaction.Type.TAXES));

        assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2017-12-31T00:00")));
        assertThat(transaction.getSource(), is("Zinsabschluss04.txt"));
        assertThat(transaction.getNote(), is("Verrechnungssteuer 01.01.2017 - 31.12.2017"));

        assertThat(transaction.getMonetaryAmount(),
                        is(Money.of("CHF", Values.Amount.factorize(140.00))));
        assertThat(transaction.getGrossValue(),
                        is(Money.of("CHF", Values.Amount.factorize(140.00))));
        assertThat(transaction.getUnitSum(Unit.Type.TAX),
                        is(Money.of("CHF", Values.Amount.factorize(0.00))));
        assertThat(transaction.getUnitSum(Unit.Type.FEE),
                        is(Money.of("CHF", Values.Amount.factorize(0.00))));

        // check fees transaction
        transaction = (AccountTransaction) results.stream().filter(TransactionItem.class::isInstance).skip(2)
                        .findFirst().orElseThrow(IllegalArgumentException::new).getSubject();

        assertThat(transaction.getType(), is(AccountTransaction.Type.FEES));

        assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2017-12-31T00:00")));
        assertThat(transaction.getSource(), is("Zinsabschluss04.txt"));
        assertThat(transaction.getNote(), is("Gebührenausweis 01.01.2017 - 31.12.2017"));

        assertThat(transaction.getMonetaryAmount(),
                        is(Money.of("CHF", Values.Amount.factorize(60.00))));
        assertThat(transaction.getGrossValue(),
                        is(Money.of("CHF", Values.Amount.factorize(60.00))));
        assertThat(transaction.getUnitSum(Unit.Type.TAX),
                        is(Money.of("CHF", Values.Amount.factorize(0.00))));
        assertThat(transaction.getUnitSum(Unit.Type.FEE),
                        is(Money.of("CHF", Values.Amount.factorize(0.00))));
    }

    @Test
    public void testKontoauszug01()
    {
        PostfinancePDFExtractor extractor = new PostfinancePDFExtractor(new Client());

        List<Exception> errors = new ArrayList<Exception>();

        List<Item> results = extractor.extract(PDFInputFile.loadTestCase(getClass(), "Kontoauszug01.txt"), errors);

        assertThat(errors, empty());
        assertThat(results.size(), is(73));

        // check transaction
        // get transactions
        Iterator<Extractor.Item> iter = results.stream().filter(i -> i instanceof TransactionItem).iterator();
        assertThat(results.stream().filter(i -> i instanceof TransactionItem).count(), is(73L));
        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-01T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(100.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Auftrag CH-DD-Basislastschrift"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-01T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(150.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Auftrag CH-DD-Basislastschrift"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-01T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(250.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Auftrag CH-DD-Basislastschrift"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-02T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(250.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Lastschrift"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-02T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(150.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Lastschrift"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-02T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(100.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Lastschrift"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-02T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(250.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Oranger Einzahlungsschein"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-02T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(150.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Oranger Einzahlungsschein"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-02T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(100.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Oranger Einzahlungsschein"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-03T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(25.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Kauf/Dienstleistung"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-03T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(15.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Kauf/Dienstleistung"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-03T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(10.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Kauf/Dienstleistung"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-04T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(2000.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Übertrag auf Konto"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-04T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(1000.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Übertrag auf Konto"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-04T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(500.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Übertrag auf Konto"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-05T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(1300.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Giro Post"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-05T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(150.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Giro Post"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-05T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(200.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Giro Post"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-06T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(1000.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Giro Bank"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-06T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(100.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Giro Bank"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-06T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(200.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Giro Bank"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-07T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(10.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Kauf/Online Shopping"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-07T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(15.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Kauf/Online Shopping"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-07T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(25.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Kauf/Online Shopping"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-07T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(150.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Bargeldbezug vom 07.04.2018"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-07T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(100.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Bargeldbezug vom 07.04.2018"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-07T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(100.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Bargeldbezug vom 07.04.2018"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-08T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(100.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Bargeldbezug"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-08T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(150.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Bargeldbezug"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-08T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(250.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Bargeldbezug"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-08T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(100.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Kauf/Dienstleistung vom 08.04.2018"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-08T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(150.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Kauf/Dienstleistung vom 08.04.2018"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-08T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(250.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Kauf/Dienstleistung vom 08.04.2018"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-08T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(100.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Kauf/Dienstleistung vom 08.04.2018"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-08T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(150.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Kauf/Dienstleistung vom 08.04.2018"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-08T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(250.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Kauf/Dienstleistung vom 08.04.2018"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-09T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(10.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Kauf/Online Shopping vom 09.04.2018"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-09T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(15.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Kauf/Online Shopping vom 09.04.2018"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-09T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(25.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Kauf/Online Shopping vom 09.04.2018"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-09T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(10.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Kauf/Online Shopping vom 09.04.2018"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-09T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(15.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Kauf/Online Shopping vom 09.04.2018"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-09T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(25.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Kauf/Online Shopping vom 09.04.2018"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-10T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(10.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("TWINT Geld senden"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-10T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(15.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("TWINT Geld senden"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-10T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(25.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("TWINT Geld senden"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-10T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(10.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("TWINT Geld senden"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-10T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(15.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("TWINT Geld senden"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-10T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(25.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("TWINT Geld senden"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-10T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(10.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("TWINT Kauf/Dienstleistung"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-10T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(15.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("TWINT Kauf/Dienstleistung"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-10T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(25.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("TWINT Kauf/Dienstleistung"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-10T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(10.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("TWINT Geld empfangen"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-10T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(15.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("TWINT Geld empfangen"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-10T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(25.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("TWINT Geld empfangen"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-20T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(10000.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Gutschrift von Fremdbank"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-20T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(600.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Gutschrift von Fremdbank"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-20T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(500.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Gutschrift von Fremdbank"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-21T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(50.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Einzahlschein/QR-Zahlteil"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-21T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(150.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Einzahlschein/QR-Zahlteil"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-21T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(250.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Einzahlschein/QR-Zahlteil"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-22T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(50.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Gutschrift Online Shopping"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-22T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(150.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Gutschrift Online Shopping"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-22T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(250.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Gutschrift Online Shopping"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-23T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(10.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Gutschrift"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-23T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(10.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Gutschrift"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-23T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(10.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Gutschrift"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.FEES));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-30T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(11.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is(""));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.FEES));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-30T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(10.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is(""));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.FEES));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-30T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(9.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is(""));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.FEES));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-30T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(5.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Kontoführungsgebühr"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.FEES));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-30T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(2.40))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Einzahlungen am Schalter"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.FEES));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-30T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(15.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Guthabengebühr für 03.2018"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.INTEREST));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-30T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(1.00))));
            assertThat(transaction.getSource(), is("Kontoauszug01.txt"));
            assertThat(transaction.getNote(), is("Zinsabschluss 01.01.2018 - 30.04.2018"));
        }
    }

    @Test
    public void testKontoauszug02()
    {
        PostfinancePDFExtractor extractor = new PostfinancePDFExtractor(new Client());

        List<Exception> errors = new ArrayList<Exception>();

        List<Item> results = extractor.extract(PDFInputFile.loadTestCase(getClass(), "Kontoauszug02.txt"), errors);

        assertThat(errors, empty());
        assertThat(results.size(), is(6));

        // check transaction
        // get transactions
        Iterator<Extractor.Item> iter = results.stream().filter(i -> i instanceof TransactionItem).iterator();
        assertThat(results.stream().filter(i -> i instanceof TransactionItem).count(), is(6L));
        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-09T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(20000.00))));
            assertThat(transaction.getSource(), is("Kontoauszug02.txt"));
            assertThat(transaction.getNote(), is("Übertrag auf Konto"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-09T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(20000.00))));
            assertThat(transaction.getSource(), is("Kontoauszug02.txt"));
            assertThat(transaction.getNote(), is("Übertrag auf Konto"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-12T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(40000.00))));
            assertThat(transaction.getSource(), is("Kontoauszug02.txt"));
            assertThat(transaction.getNote(), is("Übertrag auf Konto"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-10T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(25000.00))));
            assertThat(transaction.getSource(), is("Kontoauszug02.txt"));
            assertThat(transaction.getNote(), is("Übertrag aus Konto"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-11T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(25000.00))));
            assertThat(transaction.getSource(), is("Kontoauszug02.txt"));
            assertThat(transaction.getNote(), is("Übertrag aus Konto"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-04-13T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(10000.00))));
            assertThat(transaction.getSource(), is("Kontoauszug02.txt"));
            assertThat(transaction.getNote(), is("Übertrag aus Konto"));
        }
    }

    @Test
    public void testKontoauszug03()
    {
        PostfinancePDFExtractor extractor = new PostfinancePDFExtractor(new Client());

        List<Exception> errors = new ArrayList<Exception>();

        List<Item> results = extractor.extract(PDFInputFile.loadTestCase(getClass(), "Kontoauszug03.txt"), errors);

        assertThat(errors, empty());
        assertThat(results.size(), is(54));

        // check transaction
        // get transactions
        Iterator<Extractor.Item> iter = results.stream().filter(i -> i instanceof TransactionItem).iterator();
        assertThat(results.stream().filter(i -> i instanceof TransactionItem).count(), is(54L));
        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-01T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(250.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("E-FINANCE 01-22222-5"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-02T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(1000.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("E-FINANCE 01-22222-5"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-02T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(350.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("E-FINANCE 01-22222-5"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-03T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(250.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Auftrag DEBIT DIRECT"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-04T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(250.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Auftrag DEBIT DIRECT"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-04T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(1150.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Auftrag DEBIT DIRECT"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-05T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(200.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Kauf/Online Shopping"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-06T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(100.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Kauf/Online Shopping"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-06T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(150.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Kauf/Online Shopping"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-07T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(150.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Kauf/Online Shopping"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-08T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(100.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Kauf/Online Shopping"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-08T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(200.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Kauf/Online Shopping"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-09T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(50.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Bargeldbezug"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-10T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(100.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Bargeldbezug"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-10T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(150.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Bargeldbezug"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-11T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(50.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Online-Shopping"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-12T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(75.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Online-Shopping"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-12T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(25.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Online-Shopping"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-13T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(100.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Kauf/Dienstleistung"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-14T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(50.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Kauf/Dienstleistung"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-14T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(100.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Kauf/Dienstleistung"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-15T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(100.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Auftrag CH-DD-Basislastschrift"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-16T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(200.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Auftrag CH-DD-Basislastschrift"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-16T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(300.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Auftrag CH-DD-Basislastschrift"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-17T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(100.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Giro Internation (SEPA)"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-18T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(150.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Giro Internation (SEPA)"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-18T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(250.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Giro Internation (SEPA)"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-27T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(100.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Übertrag auf Konto"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.REMOVAL));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-27T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(250.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Übertrag auf Konto"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-22T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(100.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Giro Ausland"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-22T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(150.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Giro Ausland"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-22T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(250.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Giro Ausland"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-22T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(1300.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Giro aus Online-SIC"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-22T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(2000.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Giro aus Online-SIC"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-22T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(250.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Giro aus Online-SIC"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-23T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(1000.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Giro aus Konto"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-23T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(500.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Giro aus Konto"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-23T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(250.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Giro aus Konto"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-24T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(100.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Gutschrift Online Shopping"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-24T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(500.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Gutschrift Online Shopping"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-24T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(250.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Gutschrift Online Shopping"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-25T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(100.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Gutschrift von Fremdbank"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-25T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(500.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Gutschrift von Fremdbank"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-25T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(1250.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Gutschrift von Fremdbank"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-26T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(100.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Gutschrift von Fremdbank"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-26T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(500.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Gutschrift von Fremdbank"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-26T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(250.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Gutschrift von Fremdbank"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.DEPOSIT));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-27T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(150.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Übertrag aus Konto"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.FEES));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-30T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(1.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Überweisungsgebühr (SEPA)"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.FEES));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-30T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(2.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Kontoführungsgebühr"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.FEES));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-30T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(5.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Kontoführungsgebühr"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.FEES));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-30T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(4.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Jahrespreis Login"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.FEES));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-30T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(24.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Kontoführungsgebühr (Papier)"));
        }

        if (iter.hasNext())
        {
            Item item = iter.next();

            // assert transaction
            AccountTransaction transaction = (AccountTransaction) item.getSubject();
            assertThat(transaction.getType(), is(AccountTransaction.Type.INTEREST));
            assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2015-04-30T00:00")));
            assertThat(transaction.getMonetaryAmount(), is(Money.of("CHF", Values.Amount.factorize(1.00))));
            assertThat(transaction.getSource(), is("Kontoauszug03.txt"));
            assertThat(transaction.getNote(), is("Zinsabschluss 01.04.2015 - 30.04.2015"));
        }
    }
}
