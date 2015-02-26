package com.github.aureliano.damihilogs.parser;

public interface IParser<T> {

	public abstract T parse(String text);
	
	public abstract boolean accept(String text);
}