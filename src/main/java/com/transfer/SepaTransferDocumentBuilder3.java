package com.transfer;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.io.FileUtils;

import com.directdebit.xml.schema.pain_001_001_06.AccountIdentification4Choice;
import com.directdebit.xml.schema.pain_001_001_06.ActiveOrHistoricCurrencyAndAmount;
import com.directdebit.xml.schema.pain_001_001_06.AmountType4Choice;
import com.directdebit.xml.schema.pain_001_001_06.BranchAndFinancialInstitutionIdentification5;
import com.directdebit.xml.schema.pain_001_001_06.CashAccount24;
import com.directdebit.xml.schema.pain_001_001_06.ChargeBearerType1Code;
import com.directdebit.xml.schema.pain_001_001_06.CreditTransferTransaction20;
import com.directdebit.xml.schema.pain_001_001_06.CustomerCreditTransferInitiationV06;
import com.directdebit.xml.schema.pain_001_001_06.FinancialInstitutionIdentification8;
import com.directdebit.xml.schema.pain_001_001_06.GenericAccountIdentification1;
import com.directdebit.xml.schema.pain_001_001_06.GroupHeader48;
import com.directdebit.xml.schema.pain_001_001_06.LocalInstrument2Choice;
import com.directdebit.xml.schema.pain_001_001_06.OrganisationIdentification8;
import com.directdebit.xml.schema.pain_001_001_06.Party11Choice;
import com.directdebit.xml.schema.pain_001_001_06.PartyIdentification43;
import com.directdebit.xml.schema.pain_001_001_06.PaymentIdentification1;
import com.directdebit.xml.schema.pain_001_001_06.PaymentInstruction16;
import com.directdebit.xml.schema.pain_001_001_06.PaymentMethod3Code;
import com.directdebit.xml.schema.pain_001_001_06.PaymentTypeInformation19;
import com.directdebit.xml.schema.pain_001_001_06.PostalAddress6;
import com.directdebit.xml.schema.pain_001_001_06.RemittanceInformation10;
import com.directdebit.xml.schema.pain_001_001_06.ServiceLevel8Choice;
import com.directdebit.xml.schema.pain_001_003_03.ActiveOrHistoricCurrencyCodeEUR;
import com.util.SepaXmlDocumentBuilder;

class SepaTransferDocumentBuilder3 extends SepaXmlDocumentBuilder {

	public static String toXml(SepaTransferDocumentData source, String[] row)
			throws DatatypeConfigurationException, IOException {
		com.directdebit.xml.schema.pain_001_001_06.Document doc = new com.directdebit.xml.schema.pain_001_001_06.Document();
		CustomerCreditTransferInitiationV06 transferData = new CustomerCreditTransferInitiationV06();
		PartyIdentification43 partyIdentification43 = new PartyIdentification43();
		partyIdentification43.setNm(row[7]);
		ActiveOrHistoricCurrencyAndAmount activeOrHistoricCurrencyAndAmount = new ActiveOrHistoricCurrencyAndAmount();
		activeOrHistoricCurrencyAndAmount.setValue(new BigDecimal(row[2]));

		doc.setCstmrCdtTrfInitn(transferData);

		transferData.setGrpHdr(createGroupHeaderSdd(source));

		transferData.getGrpHdr().setInitgPty(partyIdentification43);

		transferData.getPmtInf().add(createPaymentInstructions(source));

		for (PaymentInstruction16 payment : transferData.getPmtInf()) {

			for (CreditTransferTransaction20 creditTransferTransaction20 : payment.getCdtTrfTxInf()) {
				PaymentTypeInformation19 paymentTypeInformation19 = new PaymentTypeInformation19();
				LocalInstrument2Choice localInstrument2Choice = new LocalInstrument2Choice();
				paymentTypeInformation19.setLclInstrm(localInstrument2Choice);
				creditTransferTransaction20.setPmtTpInf(paymentTypeInformation19);
				creditTransferTransaction20.getPmtTpInf().getLclInstrm().setCd(row[0]);
				creditTransferTransaction20.getAmt().setInstdAmt(activeOrHistoricCurrencyAndAmount);

				// setting
				// Document/CstmrCdtTrfInitn/PmtInf/CdtTrfTxInf/CdtrAcct/Id/Othr/Id
				CashAccount24 cashAccount24 = new CashAccount24();
				AccountIdentification4Choice accountIdentification4Choice = new AccountIdentification4Choice();
				GenericAccountIdentification1 genericAccountIdentification1 = new GenericAccountIdentification1();
				String CdtIdValue = row[6];
				CdtIdValue = CdtIdValue.replace("\"", "");
				genericAccountIdentification1.setId(CdtIdValue);
				accountIdentification4Choice.setOthr(genericAccountIdentification1);
				cashAccount24.setId(accountIdentification4Choice);
				creditTransferTransaction20.setCdtrAcct(cashAccount24);

				// setting Document/CstmrCdtTrfInitn/PmtInf/CdtTrfTxInf/Cdtr()
				String value = row[8];
				value = value.replace("\"", "");
				String cdtrDetails[] = value.split("\\|");
				PartyIdentification43 partyIdentification = new PartyIdentification43();
				partyIdentification.setNm(cdtrDetails[0]);

				PostalAddress6 postalAddress6 = new PostalAddress6();
				postalAddress6.setStrtNm(cdtrDetails[1]);
				partyIdentification.setPstlAdr(postalAddress6);

				Party11Choice party11Choice = new Party11Choice();
				OrganisationIdentification8 organisationIdentification8 = new OrganisationIdentification8();
				organisationIdentification8.setAnyBIC(cdtrDetails[2]);
				party11Choice.setOrgId(organisationIdentification8);
				partyIdentification.setId(party11Choice);

				partyIdentification.setCtryOfRes(cdtrDetails[3]);

				creditTransferTransaction20.setCdtr(partyIdentification);			

			}
			// setting Document/CstmrCdtTrfInitn/PmtInf/DbtrAcct/Id/Othr/Id(6)
			PaymentInstruction16 instruction16 = new PaymentInstruction16();
			CashAccount24 cashAccount24 = new CashAccount24();
			AccountIdentification4Choice accountIdentification4Choice = new AccountIdentification4Choice();
			GenericAccountIdentification1 genericAccountIdentification1 = new GenericAccountIdentification1();
			String DbtIdValue = row[5];
			DbtIdValue = DbtIdValue.replace("\"", "");
			genericAccountIdentification1.setId(DbtIdValue);
			accountIdentification4Choice.setOthr(genericAccountIdentification1);
			instruction16.setDbtrAcct(cashAccount24);
			cashAccount24.setId(accountIdentification4Choice);
			payment.setDbtrAcct(cashAccount24);

		}

		StringWriter resultWriter = new StringWriter();
		marshal(doc.getClass().getPackage().getName(),
				new com.directdebit.xml.schema.pain_001_001_06.ObjectFactory().createDocument(doc), resultWriter);

		FileUtils.writeStringToFile(new File("D:\\ING_Info\\ING_Repository\\com.hackathon1.demo\\pain\\"
				+ Calendar.getInstance().getTimeInMillis()), resultWriter.toString());

		return resultWriter.toString();
	}
	
	public static String toError(SepaTransferDocumentData source) throws DatatypeConfigurationException, IOException {
		com.directdebit.xml.schema.pain_001_001_06.Document doc = new com.directdebit.xml.schema.pain_001_001_06.Document();
		CustomerCreditTransferInitiationV06 transferData = new CustomerCreditTransferInitiationV06();

		doc.setCstmrCdtTrfInitn(transferData);

		transferData.setGrpHdr(createGroupHeaderSdd(source));

		transferData.getPmtInf().add(createPaymentInstructions(source));

		StringWriter resultWriter = new StringWriter();
		marshal(doc.getClass().getPackage().getName(),
				new com.directdebit.xml.schema.pain_001_001_06.ObjectFactory().createDocument(doc), resultWriter);

		FileUtils.writeStringToFile(new File("D:\\ING_Info\\ING_Repository\\com.hackathon1.demo\\error\\" + Calendar.getInstance().getTimeInMillis()),
				resultWriter.toString());

		return resultWriter.toString();
	}
	public static String toTibco(SepaTransferDocumentData source) throws DatatypeConfigurationException, IOException {
		com.directdebit.xml.schema.pain_001_001_06.Document doc = new com.directdebit.xml.schema.pain_001_001_06.Document();
		CustomerCreditTransferInitiationV06 transferData = new CustomerCreditTransferInitiationV06();

		doc.setCstmrCdtTrfInitn(transferData);

		transferData.setGrpHdr(createGroupHeaderSdd(source));

		transferData.getPmtInf().add(createPaymentInstructions(source));

		StringWriter resultWriter = new StringWriter();
		marshal(doc.getClass().getPackage().getName(),
				new com.directdebit.xml.schema.pain_001_001_06.ObjectFactory().createDocument(doc), resultWriter);

		FileUtils.writeStringToFile(new File("D:\\ING_Info\\ING_Repository\\com.hackathon1.demo\\tibco\\" + Calendar.getInstance().getTimeInMillis()),
				resultWriter.toString());

		return resultWriter.toString();
	}

	private static GroupHeader48 createGroupHeaderSdd(SepaTransferDocumentData data)
			throws DatatypeConfigurationException {
		GroupHeader48 result = new GroupHeader48();
		// message id
		result.setMsgId(data.getDocumentMessageId());

		// created on
		result.setCreDtTm(calendarToXmlGregorianCalendarDateTime(GregorianCalendar.getInstance()));

		// number of tx
		result.setNbOfTxs(String.valueOf(data.getPayments().size()));

		// control sum
		result.setCtrlSum(data.getTotalPaymentSum());

		// creditor name
		PartyIdentification43 partyIdentification43 = new PartyIdentification43();
		partyIdentification43.setNm(data.getPayerName());

		result.setInitgPty(partyIdentification43);

		return result;
	}

	private static PaymentInstruction16 createPaymentInstructions(SepaTransferDocumentData data)
			throws DatatypeConfigurationException {
		PaymentInstruction16 result = new PaymentInstruction16();
		result.setBtchBookg(data.isBatchBooking());
		result.setChrgBr(ChargeBearerType1Code.SLEV);
		result.setCtrlSum(data.getTotalPaymentSum());
		result.setNbOfTxs(String.valueOf(data.getPayments().size()));

		setPayerName(data, result);

		setPayerIbanAndBic(data, result);

		result.setPmtInfId(data.getDocumentMessageId());
		result.setPmtMtd(PaymentMethod3Code.TRF);
		result.setReqdExctnDt(calendarToXmlGregorianCalendarDateTime(data.getDateOfExecution()));

		setPaymentTypeInformation(result);

		for (SepaTransferPayment p : data.getPayments()) {
			addPaymentData(result, p);
		}

		return result;
	}

	private static void addPaymentData(PaymentInstruction16 result, SepaTransferPayment p) {
		result.getCdtTrfTxInf().add(createPaymentData(p));
	}

	private static void setPayerName(SepaTransferDocumentData data, PaymentInstruction16 result) {
		PartyIdentification43 pi2 = new PartyIdentification43();
		pi2.setNm(data.getPayerName());
		result.setDbtr(pi2);
	}

	private static void setPayerIbanAndBic(SepaTransferDocumentData data, PaymentInstruction16 result) {
		AccountIdentification4Choice ai = new AccountIdentification4Choice();
		ai.setIBAN(data.getPayerIban());
		CashAccount24 ca1 = new CashAccount24();
		ca1.setId(ai);
		result.setDbtrAcct(ca1);

		BranchAndFinancialInstitutionIdentification5 bafii = new BranchAndFinancialInstitutionIdentification5();
		FinancialInstitutionIdentification8 fii = new FinancialInstitutionIdentification8();
		fii.setBICFI(data.getPayerBic());
		bafii.setFinInstnId(fii);
		result.setDbtrAgt(bafii);
	}

	private static void setPaymentTypeInformation(PaymentInstruction16 result) {
		PaymentTypeInformation19 pti = new PaymentTypeInformation19();
		ServiceLevel8Choice sls = new ServiceLevel8Choice();
		sls.setCd("SEPA");
		pti.setSvcLvl(sls);
		result.setPmtTpInf(pti);
	}

	private static CreditTransferTransaction20 createPaymentData(SepaTransferPayment p) {
		CreditTransferTransaction20 result = new CreditTransferTransaction20();
		setPaymentCurrencyAndSum(p, result);
		setPayeeName(p, result);
		setPayeeIbanAndBic(p, result);
		setEndToEndId(p, result);
		setReasonForPayment(p, result);

		return result;
	}

	private static void setPaymentCurrencyAndSum(SepaTransferPayment p, CreditTransferTransaction20 result) {
		AmountType4Choice at = new AmountType4Choice();
		ActiveOrHistoricCurrencyAndAmount aohcaa = new ActiveOrHistoricCurrencyAndAmount();
		aohcaa.setCcy(ActiveOrHistoricCurrencyCodeEUR.EUR.toString());
		aohcaa.setValue(p.getPaymentSum());
		at.setInstdAmt(aohcaa);
		result.setAmt(at);
	}

	private static void setPayeeName(SepaTransferPayment p, CreditTransferTransaction20 result) {
		PartyIdentification43 pis2 = new PartyIdentification43();
		pis2.setNm(p.getPayeeName());
		result.setCdtr(pis2);
	}

	private static void setEndToEndId(SepaTransferPayment p, CreditTransferTransaction20 result) {
		PaymentIdentification1 pis = new PaymentIdentification1();
		String id = p.getEndToEndId();
		pis.setEndToEndId(id == null || id.isEmpty() ? "NOTPROVIDED" : "");
		result.setPmtId(pis);
	}

	private static void setReasonForPayment(SepaTransferPayment p, CreditTransferTransaction20 result) {
		RemittanceInformation10 ri = new RemittanceInformation10();
		// ri.getStrd().get(0).sete(p.getReasonForPayment());
		result.setRmtInf(ri);
	}

	private static void setPayeeIbanAndBic(SepaTransferPayment p, CreditTransferTransaction20 ctti) {
		CashAccount24 ca = new CashAccount24();
		AccountIdentification4Choice ai = new AccountIdentification4Choice();
		ai.setIBAN(p.getPayeeIban());
		ca.setId(ai);
		ctti.setCdtrAcct(ca);

		BranchAndFinancialInstitutionIdentification5 bafiis = new BranchAndFinancialInstitutionIdentification5();
		FinancialInstitutionIdentification8 fii = new FinancialInstitutionIdentification8();
		fii.setBICFI(p.getPayeeBic());
		bafiis.setFinInstnId(fii);
		ctti.setCdtrAgt(bafiis);
	}

	private static XMLGregorianCalendar convertDate(String inputDate){
		XMLGregorianCalendar result = null;
		Date date1;
		SimpleDateFormat simpleDateFormat;
		GregorianCalendar gregorianCalendar;
		simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		try{
			date1 = simpleDateFormat.parse(inputDate);
			gregorianCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
			gregorianCalendar.setTime(date1);
			result = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
			System.out.println("inputDate" + inputDate + "--->" + result);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
}
