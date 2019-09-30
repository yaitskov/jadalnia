package org.dan.jadalnia.app.token

import org.dan.jadalnia.app.ws.MessageForClient

data class TokenApprovedEvent(val tokenId: TokenId): MessageForClient
