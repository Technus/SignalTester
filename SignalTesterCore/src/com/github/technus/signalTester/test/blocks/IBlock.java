package com.github.technus.signalTester.test.blocks;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@BsonDiscriminator
public interface IBlock {
    IBlockIdentifier getIdentifier();
}
