package com.github.technus.signalTester.test.blocks;

public interface IBlockIdentifier extends Comparable<IBlockIdentifier> {
    @Override
    default int compareTo(IBlockIdentifier o) {
        return this.toString().compareTo(o.toString());
    }
}
