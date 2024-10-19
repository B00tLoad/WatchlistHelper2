import { createTRPCRouter, protectedProcedure } from "~/server/api/trpc";
import { z } from "zod";
import { ProviderSchema } from "../../../../prisma/generated/zod";

export const providerRouter = createTRPCRouter({
  getAll: protectedProcedure
    .input(
      z.object({
        count: z.number().optional(),
        name: z.string().min(3, "Provide at least 3 letters.").optional(),
      }),
    )
    .query(async ({ ctx, input }) => {
      if (input.count && input.name)
        return ctx.db.provider.findMany({
          where: { name: input.name },
          take: input.count,
        });
      if (input.count)
        return ctx.db.provider.findMany({
          take: input.count,
        });
      if (input.name)
        return ctx.db.provider.findMany({
          where: { name: input.name },
        });
      return ctx.db.provider.findMany();
    }),
  create: protectedProcedure
    .input(ProviderSchema.omit({ id: true }))
    .mutation(async ({ ctx, input }) => {
      return ctx.db.provider.create({
        data: {
          name: input.name,
          icon: input.icon,
          url: input.url,
          requires_subscription: input.requires_subscription,
        },
      });
    }),
  getVideoProviders: protectedProcedure
    .input(z.object({
      id: z.string(),
      freeOnly: z.boolean()
    }))
    .query(async ({ ctx, input }) => {
      if (input.freeOnly) {
        return ctx.db.availability.findMany({
          where: {
            entryId: input.id,
            provider: {
              requires_subscription: false
            }
          },
          select: {
            url: true,
            provider: true
          }
        })
      }
      return ctx.db.availability.findMany({
        where: {
          entryId: input.id,
        },
        select: {
          url: true,
          provider: true
        }
      })
    })
});
