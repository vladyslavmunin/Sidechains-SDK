package com.horizen.block

import com.horizen.utils.{ByteArrayWrapper, BytesUtils}
import org.junit.Assert.{assertEquals, assertTrue}
import org.junit.Test
import org.scalatest.junit.JUnitSuite

import scala.io.Source

// tx data in RPC byte order: https://explorer.zen-solutions.io/api/rawtx/<tx_id>
class MainchainTransactionTest extends JUnitSuite {

  @Test
  def MainchainTransactionTest_tx_v1_coinbase(): Unit = {
    val hex : String = Source.fromResource("mctx_v1_coinbase").getLines().next()
    val bytes: Array[Byte] = BytesUtils.fromHexString(hex)

    val tx: MainchainTransaction = MainchainTransaction.create(bytes, 0).get

    assertEquals("Tx Hash is different.", "e2f681e0431bc5f77299373632350ac493211fa8f3d0491a2d6c5e0284f5d377", tx.hashHex)
    assertEquals("Tx Size is different.", 190, tx.size)
  }

  @Test
  def MainchainTransactionTest_tx_v1(): Unit = {
    val hex : String = Source.fromResource("mctx_v1").getLines().next()
    val bytes: Array[Byte] = BytesUtils.fromHexString(hex)

    val tx: MainchainTransaction = MainchainTransaction.create(bytes, 0).get

    assertEquals("Tx Hash is different.", "6054f092033e5bb352d46ddb837b10da91eb43b40da46656e46140e3ce938db9", tx.hashHex)
    assertEquals("Tx Size is different.", 9150, tx.size)
  }

  @Test
  def MainchainTransactionTest_tx_v2(): Unit = {
    val hex : String = Source.fromResource("mctx_v2").getLines().next()
    val bytes: Array[Byte] = BytesUtils.fromHexString(hex)

    val tx: MainchainTransaction = MainchainTransaction.create(bytes, 0).get

    assertEquals("Tx Hash is different.", "3c1bd6d388a731016fbc809ab032b35356e64b2e7601ede00fd430fb612937ac", tx.hashHex)
    assertEquals("Tx Size is different.", 1909, tx.size)
  }

  @Test
  def MainchainTransactionTest_tx_vminus3(): Unit = {
    val hex : String = Source.fromResource("mctx_v-3").getLines().next()
    val bytes: Array[Byte] = BytesUtils.fromHexString(hex)

    val tx: MainchainTransaction = MainchainTransaction.create(bytes, 0).get

    assertEquals("Tx Hash is different.", "dee5a3758cee29648a6a50edf26c56db60c1186e434302299fd0f3e8339bf45a", tx.hashHex)
    assertEquals("Tx Size is different.", 1953, tx.size)
  }

  @Test
  def tx_vminus4_single_ft(): Unit = {
    val hex : String = Source.fromResource("mctx_v-4_single_ft").getLines().next()
    val bytes: Array[Byte] = BytesUtils.fromHexString(hex)

    val tx: MainchainTransaction = MainchainTransaction.create(bytes, 0).get
    val sidechainIdHex: String = "0000000000000000000000000000000000000000000000000000000000000002"
    val sidechainId: ByteArrayWrapper = new ByteArrayWrapper(BytesUtils.fromHexString(sidechainIdHex))


    assertEquals("Tx Hash is different.", "a94c3ccab35f75ce96ce979f95492678a857d69307e9d7079c2068ee0b41faff", tx.hashHex)
    assertEquals("Tx Size is different.", 302, tx.size)
    assertEquals("Tx contains different number of mentioned sidechains.", 1, tx.getRelatedSidechains.size)
    assertTrue(s"SidechainId '${sidechainIdHex}' is missed.",
      tx.getRelatedSidechains.contains(sidechainId))

    val crosschainOutputs: Seq[MainchainTxCrosschainOutput] = tx.getCrosschainOutputs(sidechainId)
    assertEquals(s"Tx expected to have different number of crosschain outputs related to sidechainId '${sidechainIdHex}'.", 1, crosschainOutputs.size)
    assertTrue("Crosschain output type is different.", crosschainOutputs.head.isInstanceOf[MainchainTxForwardTransferCrosschainOutput])

    val ft: MainchainTxForwardTransferCrosschainOutput = crosschainOutputs.head.asInstanceOf[MainchainTxForwardTransferCrosschainOutput]
    assertEquals("Forward Transfer type is different.", MainchainTxForwardTransferCrosschainOutput.OUTPUT_TYPE, ft.outputType)
    assertEquals("Forward Transfer hash is different.","8f57131e8c27f8ea6bc8fb28a025c9107569d44541b95371cc36f24a612f44ec", BytesUtils.toHexString(ft.hash))
    assertEquals("Forward Transfer sc id is different.", sidechainIdHex, BytesUtils.toHexString(ft.sidechainId))
    assertEquals("Forward Transfer proposition is different.","000000000000000000000000000000000000000000000000000000000002add3", BytesUtils.toHexString(ft.propositionBytes))
    assertEquals("Forward Transfer amount is different.", 1000000, ft.amount)
  }

  @Test
  def tx_vminus4_multiple_ft(): Unit = {
    /*val hex : String = Source.fromResource("mctx_v-4_multiple_ft").getLines().next()
    val bytes: Array[Byte] = BytesUtils.fromHexString(hex)

    val tx: MainchainTransaction = MainchainTransaction.create(bytes, 0).get
    val sidechainIdHex: String = "0000000000000000000000000000000000000000000000000000000000000002"
    val sidechainId: ByteArrayWrapper = new ByteArrayWrapper(BytesUtils.fromHexString(sidechainIdHex))


    assertEquals("Tx Hash is different.", "a94c3ccab35f75ce96ce979f95492678a857d69307e9d7079c2068ee0b41faff", tx.hashHex)
    assertEquals("Tx Size is different.", 302, tx.size)
    assertEquals("Tx contains different number of mentioned sidechains.", 1, tx.getRelatedSidechains.size)
    assertTrue(s"SidechainId '${sidechainIdHex}' is missed.",
      tx.getRelatedSidechains.contains(sidechainId))

    val crosschainOutputs: Seq[MainchainTxCrosschainOutput] = tx.getCrosschainOutputs(sidechainId)
    assertEquals(s"Tx expected to have different number of crosschain outputs related to sidechainId '${sidechainIdHex}'.", 1, crosschainOutputs.size)
    assertTrue("Crosschain output type is different.", crosschainOutputs.head.isInstanceOf[MainchainTxForwardTransferCrosschainOutput])

    val ft: MainchainTxForwardTransferCrosschainOutput = crosschainOutputs.head.asInstanceOf[MainchainTxForwardTransferCrosschainOutput]
    assertEquals("Forward Transfer type is different.", MainchainTxForwardTransferCrosschainOutput.OUTPUT_TYPE, ft.outputType)
    assertEquals("Forward Transfer hash is different.","8f57131e8c27f8ea6bc8fb28a025c9107569d44541b95371cc36f24a612f44ec", BytesUtils.toHexString(ft.hash))
    assertEquals("Forward Transfer sc id is different.", sidechainIdHex, BytesUtils.toHexString(ft.sidechainId))
    assertEquals("Forward Transfer proposition is different.","000000000000000000000000000000000000000000000000000000000002add3", BytesUtils.toHexString(ft.propositionBytes))
    assertEquals("Forward Transfer amount is different.", 1000000, ft.amount)*/
  }

  @Test
  def tx_vminus4_sc_creation_with_ft(): Unit = {
    val hex : String = Source.fromResource("mctx_v-4_sc_creation").getLines().next()
    val bytes: Array[Byte] = BytesUtils.fromHexString(hex)

    val tx: MainchainTransaction = MainchainTransaction.create(bytes, 0).get
    val sidechainIdHex: String = "0000000000000000000000000000000000000000000000000000000000000001"
    val sidechainId: ByteArrayWrapper = new ByteArrayWrapper(BytesUtils.fromHexString(sidechainIdHex))


    assertEquals("Tx Hash is different.", "15c69f106e6c9aa0e864b66106dc641af6a04d4a373c97e71fb194f32d8d1d4e", tx.hashHex)
    assertEquals("Tx Size is different.", 548, tx.size)
    assertEquals("Tx contains different number of mentioned sidechains.", 1, tx.getRelatedSidechains.size)
    assertTrue(s"SidechainId '${sidechainIdHex}' is missed.",
      tx.getRelatedSidechains.contains(sidechainId))

    val crosschainOutputs: Seq[MainchainTxCrosschainOutput] = tx.getCrosschainOutputs(sidechainId)
    assertEquals(s"Tx expected to have different number of crosschain outputs related to sidechainId '${sidechainIdHex}'.", 4, crosschainOutputs.size)


    assertTrue("Crosschain output type is different.", crosschainOutputs.head.isInstanceOf[MainchainTxSidechainCreationCrosschainOutput])
    val creation: MainchainTxSidechainCreationCrosschainOutput = crosschainOutputs.head.asInstanceOf[MainchainTxSidechainCreationCrosschainOutput]
    assertEquals("Sidechain creation type is different.", MainchainTxSidechainCreationCrosschainOutput.OUTPUT_TYPE, creation.outputType)
    assertEquals("Sidechain creation hash is different.","4324f4a00c6b8b5d0ca58c2f741f0208436bff260ecb724011db47a70ed4cd1f", BytesUtils.toHexString(creation.hash))
    assertEquals("Sidechain creation sc id is different.", sidechainIdHex, BytesUtils.toHexString(creation.sidechainId))
    assertEquals("Sidechain creation withdrawal epoch length is different.", 100, creation.withdrawalEpochLength)

    assertTrue("Crosschain output type is different.", crosschainOutputs(1).isInstanceOf[MainchainTxForwardTransferCrosschainOutput])
    var ft: MainchainTxForwardTransferCrosschainOutput = crosschainOutputs(1).asInstanceOf[MainchainTxForwardTransferCrosschainOutput]
    assertEquals("Forward Transfer type is different.", MainchainTxForwardTransferCrosschainOutput.OUTPUT_TYPE, ft.outputType)
    assertEquals("Forward Transfer hash is different.","c8186f70d65fbe6b58a004f3452bbba43f491e6b48f910a35fa024ac5d20c5f6", BytesUtils.toHexString(ft.hash))
    assertEquals("Forward Transfer sc id is different.", sidechainIdHex, BytesUtils.toHexString(ft.sidechainId))
    assertEquals("Forward Transfer proposition is different.","000000000000000000000000000000000000000000000000000000000000add1", BytesUtils.toHexString(ft.propositionBytes))
    assertEquals("Forward Transfer amount is different.", 10000000, ft.amount)

    assertTrue("Crosschain output type is different.", crosschainOutputs(2).isInstanceOf[MainchainTxForwardTransferCrosschainOutput])
    ft = crosschainOutputs(2).asInstanceOf[MainchainTxForwardTransferCrosschainOutput]
    assertEquals("Forward Transfer type is different.", MainchainTxForwardTransferCrosschainOutput.OUTPUT_TYPE, ft.outputType)
    assertEquals("Forward Transfer hash is different.","91a101770761eccd4e2d68522bd57f065fb0cb848026f47f23e4cdbea2c9aba5", BytesUtils.toHexString(ft.hash))
    assertEquals("Forward Transfer sc id is different.", sidechainIdHex, BytesUtils.toHexString(ft.sidechainId))
    assertEquals("Forward Transfer proposition is different.","000000000000000000000000000000000000000000000000000000000000add2", BytesUtils.toHexString(ft.propositionBytes))
    assertEquals("Forward Transfer amount is different.", 20000000, ft.amount)

    assertTrue("Crosschain output type is different.", crosschainOutputs(3).isInstanceOf[MainchainTxForwardTransferCrosschainOutput])
    ft = crosschainOutputs(3).asInstanceOf[MainchainTxForwardTransferCrosschainOutput]
    assertEquals("Forward Transfer type is different.", MainchainTxForwardTransferCrosschainOutput.OUTPUT_TYPE, ft.outputType)
    assertEquals("Forward Transfer hash is different.","924296ac92ed1f9ae20bea137a07ab427ee10897385702becc7b2dc34109ab70", BytesUtils.toHexString(ft.hash))
    assertEquals("Forward Transfer sc id is different.", sidechainIdHex, BytesUtils.toHexString(ft.sidechainId))
    assertEquals("Forward Transfer proposition is different.","000000000000000000000000000000000000000000000000000000000000add3", BytesUtils.toHexString(ft.propositionBytes))
    assertEquals("Forward Transfer amount is different.", 30000000, ft.amount)
  }
}
