package com.yodlee.sampleapps;

import java.io.Serializable;

import com.yodlee.soap.core.dataservice.YMoney;

public class BudgetGoal implements Serializable {
	
	private String category;
	private Long categoryId;
	private YMoney current;
	private YMoney average;
	private String goalCurrency;
	private String goal;
	private boolean reportable;
	private boolean alertable;
	private boolean budgetable = true;
	private boolean includeInTotalCalculation = true;
	private long categoryTypeId;

	private YMoney expenditure;
	private YMoney income;
	private YMoney averageExpenditure;
	private YMoney averageIncome;

	private boolean existing;

	private String categoryTypeName;
	private Long transactionCategoryTypeId;
	private long parentCategoryId;
	private long categoryLevelId;
	private BudgetGoal[] childCategory;
	private int leftIndent; // FOR SETTING INDENT WHILE DISPLAYING
	private long grandParentCategoryId; // FOR EXPAND AND COLLAPSE WHILE
										// DISPLAYING
	private boolean categoryFlag; // FOR EXPAND AND COLLAPSE WHILE DISPLAYING
	private boolean parentCategoryFlag;
	private boolean categoryTypeFlag;
	private boolean thisCategoryFlag;
	private boolean parent;
	private boolean child;

	public BudgetGoal() {
		super();
	}

	public BudgetGoal(String category, YMoney currentSpending,
			YMoney averageSpending) {
		this.category = category;
		this.current = currentSpending;
		this.average = averageSpending;
	}

	/**
	 * @return Returns the alertable.
	 */
	public boolean getAlertable() {
		return alertable;
	}

	/**
	 * @param alertable
	 *            The alertable to set.
	 */
	public void setAlertable(boolean alertable) {
		this.alertable = alertable;
	}

	/**
	 * @return Returns the averageSpending.
	 */
	public YMoney getAverage() {
		return average;
	}

	/**
	 * @param averageSpending
	 *            The averageSpending to set.
	 */
	public void setAverage(YMoney averageSpending) {
		this.average = averageSpending;
	}

	/**
	 * @return Returns the budgetGoalThreshold.
	 */
	public String getGoal() {
		return goal;
	}

	/**
	 * @param budgetGoalThreshold
	 *            The budgetGoalThreshold to set.
	 */
	public void setGoal(String budgetGoalThreshold) {
		this.goal = budgetGoalThreshold;
	}

	/**
	 * @return Returns the thresholdCurrency.
	 */
	public String getGoalCurrency() {
		return goalCurrency;
	}

	/**
	 * @param thresholdCurrency
	 *            The thresholdCurrency to set.
	 */
	public void setGoalCurrency(String thresholdCurrency) {
		this.goalCurrency = thresholdCurrency;
	}

	/**
	 * @return Returns the category.
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category
	 *            The category to set.
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return Returns the currentSpending.
	 */
	public YMoney getCurrent() {
		return current;
	}

	/**
	 * @param currentSpending
	 *            The currentSpending to set.
	 */
	public void setCurrent(YMoney currentSpending) {
		this.current = currentSpending;
	}

	/**
	 * @return Returns the reportable.
	 */
	public boolean getReportable() {
		return reportable;
	}

	/**
	 * @param reportable
	 *            The reportable to set.
	 */
	public void setReportable(boolean reportable) {
		this.reportable = reportable;
	}

	/**
	 * @return Returns the expenditure.
	 */
	public YMoney getExpenditure() {
		return expenditure;
	}

	/**
	 * @param expenditure
	 *            The expenditure to set.
	 */
	public void setExpenditure(YMoney expenditure) {
		this.expenditure = expenditure;
	}

	/**
	 * @return Returns the income.
	 */
	public YMoney getIncome() {
		return income;
	}

	/**
	 * @param income
	 *            The income to set.
	 */
	public void setIncome(YMoney income) {
		this.income = income;
	}

	/**
	 * @return Returns the averageExpenditure.
	 */
	public YMoney getAverageExpenditure() {
		return averageExpenditure;
	}

	/**
	 * @param averageExpenditure
	 *            The averageExpenditure to set.
	 */
	public void setAverageExpenditure(YMoney averageExpenditure) {
		this.averageExpenditure = averageExpenditure;
	}

	/**
	 * @return Returns the averageIncome.
	 */
	public YMoney getAverageIncome() {
		return averageIncome;
	}

	/**
	 * @param averageIncome
	 *            The averageIncome to set.
	 */
	public void setAverageIncome(YMoney averageIncome) {
		this.averageIncome = averageIncome;
	}

	/**
	 * @return Returns the categoryId.
	 */
	public Long getCategoryId() {
		return categoryId;
	}

	/**
	 * @param categoryId
	 *            The categoryId to set.
	 */
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * @return Returns the existing.
	 */
	public boolean isExisting() {
		return existing;
	}

	/**
	 * @param existing
	 *            The existing to set.
	 */
	public void setExisting(boolean existing) {
		this.existing = existing;
	}

	/**
	 * @return Returns the budgetable.
	 */
	public boolean isBudgetable() {
		return budgetable;
	}

	/**
	 * @param budgetable
	 *            The budgetable to set.
	 */
	public void setBudgetable(boolean budgetable) {
		this.budgetable = budgetable;
	}

	/**
	 * @return Returns the includeInTotalCalculation.
	 */
	public boolean isIncludeInTotalCalculation() {
		return includeInTotalCalculation;
	}

	/**
	 * @param includeInTotalCalculation
	 *            The includeInTotalCalculation to set.
	 */
	public void setIncludeInTotalCalculation(boolean includeInTotalCalculation) {
		this.includeInTotalCalculation = includeInTotalCalculation;
	}

	public long getCategoryTypeId() {
		return categoryTypeId;
	}

	public void setCategoryTypeId(long categoryTypeId) {
		this.categoryTypeId = categoryTypeId;
	}

	public String getCategoryTypeName() {
		return categoryTypeName;
	}

	public void setCategoryTypeName(String categoryTypeName) {
		this.categoryTypeName = categoryTypeName;
	}

	public long getParentCategoryId() {
		return parentCategoryId;
	}

	public void setParentCategoryId(long parentCategoryId) {
		this.parentCategoryId = parentCategoryId;
	}

	public long getCategoryLevelId() {
		return categoryLevelId;
	}

	public void setCategoryLevelId(long categoryLevelId) {
		this.categoryLevelId = categoryLevelId;
	}

	public BudgetGoal[] getChildCategory() {
		return childCategory;
	}

	public void setChildCategory(BudgetGoal[] childCategory) {
		this.childCategory = childCategory;
	}

	public int getLeftIndent() {
		return leftIndent;
	}

	public void setLeftIndent(int leftIndent) {
		this.leftIndent = leftIndent;
	}

	public long getGrandParentCategoryId() {
		return grandParentCategoryId;
	}

	public void setGrandParentCategoryId(long grandParentCategoryId) {
		this.grandParentCategoryId = grandParentCategoryId;
	}

	public Long getTransactionCategoryTypeId() {
		return transactionCategoryTypeId;
	}

	public void setTransactionCategoryTypeId(long transactionCategoryTypeId) {
		this.transactionCategoryTypeId = new Long(transactionCategoryTypeId);
	}

	public boolean isCategoryTypeFlag() {
		return categoryTypeFlag;
	}

	public void setCategoryTypeFlag(boolean categoryTypeFlag) {
		this.categoryTypeFlag = categoryTypeFlag;
	}

	public boolean isCategoryFlag() {
		return categoryFlag;
	}

	public void setCategoryFlag(boolean categoryFlag) {
		this.categoryFlag = categoryFlag;
	}

	public boolean isThisCategoryFlag() {
		return thisCategoryFlag;
	}

	public void setThisCategoryFlag(boolean thisCategoryFlag) {
		this.thisCategoryFlag = thisCategoryFlag;
	}

	public boolean isParentCategoryFlag() {
		return parentCategoryFlag;
	}

	public void setParentCategoryFlag(boolean parentCategoryFlag) {
		this.parentCategoryFlag = parentCategoryFlag;
	}

	public boolean isParent() {
		return parent;
	}

	public void setParent(boolean isParent) {
		this.parent = isParent;
	}

	public boolean isChild() {
		return child;
	}

	public void setChild(boolean isChild) {
		this.child = isChild;
	}
}
