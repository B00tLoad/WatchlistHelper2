import { z } from "zod";

import {
    createTRPCRouter,
    protectedProcedure
} from "~/server/api/trpc";

export const userRouter = createTRPCRouter({
    discord_id: protectedProcedure
        .query(async ({ ctx }) => {
            return ctx.db.user.findUniqueOrThrow({
                where: {id: ctx.session.user.id},
            }).accounts({
                where: {provider: 'discord'},
                select: {
                    providerAccountId: true
                }
            })
        }),
  getById: protectedProcedure
    .input(z.object({id: z.string()}))
    .query(async ({ ctx, input }) => {
      return ctx.db.user.findUniqueOrThrow({
        where: {id: input.id }
      })
    })
})