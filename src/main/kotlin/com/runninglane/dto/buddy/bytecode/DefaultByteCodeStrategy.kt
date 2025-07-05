package com.runninglane.dto.buddy.bytecode

import com.runninglane.dto.buddy.bytecode.bytebuddy.ByteBuddyByteCodeStrategy

class DefaultByteCodeStrategy : ByteCodeStrategy by ByteBuddyByteCodeStrategy()