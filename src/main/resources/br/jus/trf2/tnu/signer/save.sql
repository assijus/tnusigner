-- grava a assinatura e atualiza a situacao.
update ProcessoDocumento set detached = ? where	processoId = ? and documentoId = ?