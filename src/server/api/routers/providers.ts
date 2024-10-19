import {createTRPCRouter, protectedProcedure} from "~/server/api/trpc";
import {z} from "zod"
import {EntryTypeSchema, ProviderSchema} from "../../../../prisma/generated/zod";

export const entryRouter = createTRPCRouter({
    getAll: protectedProcedure
        .input(
            z.object({
                count: z.number().optional(),
                type: EntryTypeSchema.optional(),
            }),
        )
        .query(async ({ ctx, input }) => {
            if (input.count && input.type)
                return ctx.db.provider.findMany({
                    where: { type: input.type },
                    take: input.count,
                });
            if (input.count)
                return ctx.db.provider.findMany({
                    take: input.count,
                });
            if (input.type)
                return ctx.db.provider.findMany({
                    where: { type: input.type },
                });
            return ctx.db.provider.findMany();
        }),
        create: protectedProcedure
            .input(ProviderSchema)
            .mutation(async ({ctx, input}) =>)
})